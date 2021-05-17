package com.shortcircuit.utils.bukkit.command;

import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.google.common.base.Joiner;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;

public abstract class BaseCommand<T extends CommandSender> extends Command implements PluginIdentifiableCommand
{
    private static final Joiner default_joiner = Joiner.on("\n");
    private final TieredKits plugin;

    protected BaseCommand(TieredKits plugin)
    {
        super(plugin.getClass().getSimpleName() + ":tmp");
        this.plugin = plugin;
        this.setName(this.getCommandName());
        if (this.getCommandAliases() != null)
        {
            this.setAliases(Arrays.asList(this.getCommandAliases()));
        }

        if (this.getCommandDescription() != null)
        {
            this.setDescription(this.alternateLines(this.getCommandDescription()));
        }

        if (this.getCommandUsage() != null)
        {
            this.setUsage(this.alternateLines(this.getCommandUsage()));
        }

        this.setPermission(this.getCommandPermission());
        if (this.getCommandPermissionMessage() != null)
        {
            this.setPermissionMessage(default_joiner.join(this.getCommandPermissionMessage()));
        }

    }

    public final boolean testPermissionSilent(CommandSender target)
    {
        return PermissionUtils.hasPermission(target, this.getPermission());
    }

    public abstract String getCommandName();

    public abstract String[] getCommandAliases();

    public abstract CommandType getCommandType();

    public abstract String[] getCommandDescription();

    public abstract String[] getCommandUsage();

    public abstract String getCommandPermission();

    public String[] getCommandPermissionMessage()
    {
        return null;
    }

    private String alternateLines(String[] lines)
    {
        boolean color = false;
        StringBuilder builder = new StringBuilder();

        for (String line : lines)
        {
            builder.append(color ? ChatColor.GRAY : ChatColor.WHITE).append(line).append("\n");
            color ^= true;
        }

        return builder.toString().trim();
    }

    public abstract String[] exec(T sender, String command, ConcurrentArrayList<String> list) throws
            CommandException;

    public final boolean execute(CommandSender sender, String command, String[] args)
    {
        if (this.getCommandType() == null)
        {
            sender.sendMessage(ChatColor.RED + "This command is not applicable to any users");
        }

        if (!this.getCommandType().isSenderApplicable(sender))
        {
            sender.sendMessage(
                    ChatColor.RED + "This command is " + this.getCommandType().getApplicableSenderName() + "-only");
            return false;
        }
        else
        {
            if (this.getCommandType() == CommandType.LOCATABLE)
            {
                sender = new LocatableCommandSender(sender);
            }

            if (!this.testPermission(sender))
            {
                return false;
            }
            else
            {
                try
                {
                    String[] result = this.exec((T)sender, command, new ConcurrentArrayList<>(args));
                    if (result != null)
                    {
                        for (String message : result)
                        {
                            sender.sendMessage(ChatColor.AQUA + message);
                        }
                    }
                }
                catch (CommandException e)
                {
                    sender.sendMessage(ChatColor.RED + e.getMessage());
                }

                return true;
            }
        }
    }

    public TieredKits getPlugin()
    {
        return this.plugin;
    }

    protected static synchronized boolean checkSubcommand(String subcommand, ConcurrentArrayList<String> args)
    {
        String[] path = subcommand.split("\\s");
        if (args.size() < path.length)
        {
            return false;
        }
        else
        {
            int i;
            for (i = 0; i < path.length; ++i)
            {
                String part = path[i];
                String arg = args.get(i);
                if (!part.matches(arg))
                {
                    return false;
                }
            }

            List<String> tmp = args.subList(i, args.size());
            args.clear();
            args.addAll(tmp);
            return true;
        }
    }

    public boolean setName(String name)
    {
        try
        {
            Method set_name = Command.class.getDeclaredMethod("setName", String.class);
            set_name.invoke(this, name);
            return true;
        }
        catch (ReflectiveOperationException var6)
        {
            try
            {
                Field name_field = Command.class.getDeclaredField("name");
                Field modifiers_field = Field.class.getDeclaredField("modifiers");
                modifiers_field.setAccessible(true);
                modifiers_field.setInt(name_field, modifiers_field.getInt(name_field) & 16);
                name_field.setAccessible(true);
                name_field.set(this, name);
                return true;
            }
            catch (ReflectiveOperationException var5)
            {
                this.plugin.getLogger().warning("Unable to set command name:");
                this.plugin.getLogger().warning(var5.getClass().getCanonicalName() + ": " + var5.getMessage());
                return false;
            }
        }
    }
}
