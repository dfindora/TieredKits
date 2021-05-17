package com.shortcircuit.utils.bukkit.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

public class CommandRegister {
    private static final LinkedList<BaseCommand<?>> register_queue = new LinkedList<>();
    private static final LinkedList<BaseCommand<?>> unregister_queue = new LinkedList<>();
    private static CommandRegister.LateCommandRegisterTask late_register_task;
    private static CommandMap command_map = null;

    public CommandRegister() {
    }

    public static synchronized void delegateLateRegistration(Plugin plugin) {
        if (!plugin.isEnabled()) {
            plugin.getLogger().warning("Cannot delegate late registration to disabled plugin");
        }

        if (late_register_task != null) {
            plugin.getLogger().info("Late registration has already been delegated");
        } else {
            late_register_task = new CommandRegister.LateCommandRegisterTask();
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, late_register_task, 0L);
        }
    }

    public static synchronized boolean register(BaseCommand<?> command) {
        command.getPlugin().getLogger().info("Registering command: " + command.getClass().getCanonicalName());
        CommandMap command_map = getBukkitCommandMap();
        if (command_map == null) {
            command.getPlugin().getLogger().warning("Could not access server command map");
            command.getPlugin().getLogger().warning("Command will be queued for later registration");
            synchronized(register_queue) {
                register_queue.add(command);
                return false;
            }
        } else {
            return command_map.register(command.getPlugin().getName().replaceAll("\\s", ""), command);
        }
    }

    public static synchronized void registerAll(Plugin plugin, String package_name) {
        plugin.getLogger().info("Scanning for commands in package: " + package_name);
        Reflections reflections = new Reflections(package_name, plugin.getClass().getClassLoader());
        Set<Class<? extends BaseCommand>> command_classes = reflections.getSubTypesOf(BaseCommand.class);
        Iterator<Class<? extends BaseCommand>> classIterator = command_classes.iterator();

        while(true) {
            Class<? extends BaseCommand> command_class;
            do {
                do {
                    if (!classIterator.hasNext()) {
                        return;
                    }

                    command_class = classIterator.next();
                } while(Modifier.isAbstract(command_class.getModifiers()));
            } while(Modifier.isInterface(command_class.getModifiers()));

            Constructor<? extends BaseCommand> ctor;
            try {
                try {
                    ctor = command_class.getDeclaredConstructor(Plugin.class);
                    ctor.setAccessible(true);
                } catch (NoSuchMethodException var9) {
                    ctor = command_class.getDeclaredConstructor(plugin.getClass());
                    ctor.setAccessible(true);
                }
            } catch (ReflectiveOperationException var11) {
                plugin.getLogger().warning("Command class " + command_class.getCanonicalName() + " does not have a valid constructor");
                continue;
            }

            BaseCommand<?> command;
            try {
                command = ctor.newInstance(plugin);
            } catch (ReflectiveOperationException var10) {
                plugin.getLogger().warning("Could not instantiate command: " + command_class.getCanonicalName());
                plugin.getLogger().warning(var10.getClass().getCanonicalName() + ": " + var10.getMessage());
                continue;
            }

            register(command);
        }
    }

    public static boolean unregister(BaseCommand<?> command) {
        command.getPlugin().getLogger().info("Unregistering command: " + command.getClass().getCanonicalName());
        CommandMap command_map = getBukkitCommandMap();
        if (command_map == null) {
            command.getPlugin().getLogger().warning("Could not access server command map");
            command.getPlugin().getLogger().warning("Command will be queued for later unregistration");
            synchronized(unregister_queue) {
                unregister_queue.add(command);
                return false;
            }
        } else {
            return command.unregister(command_map);
        }
    }

    private static CommandMap getBukkitCommandMap() {
        if (command_map == null) {
            try {
                Field field = Bukkit.getServer().getPluginManager().getClass().getDeclaredField("commandMap");
                field.setAccessible(true);
                command_map = (CommandMap)field.get(Bukkit.getServer().getPluginManager());
            } catch (ReflectiveOperationException var1) {
                return null;
            }
        }

        return command_map;
    }

    static {
        getBukkitCommandMap();
    }

    private static class LateCommandRegisterTask implements Runnable {
        private LateCommandRegisterTask() {
        }

        public void run() {
            synchronized(CommandRegister.register_queue) {
                Iterator<BaseCommand<?>> iterator = CommandRegister.register_queue.iterator();
                CommandRegister.register_queue.clear();

                while(true) {
                    if (!iterator.hasNext()) {
                        iterator = CommandRegister.unregister_queue.iterator();
                        CommandRegister.unregister_queue.clear();

                        while(iterator.hasNext()) {
                            CommandRegister.unregister(iterator.next());
                        }
                        break;
                    }

                    CommandRegister.register(iterator.next());
                }
            }

            CommandRegister.late_register_task = null;
        }
    }
}
