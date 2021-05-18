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
    private static final Joiner defaultJoiner = Joiner.on("\n");
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
            this.setPermissionMessage(defaultJoiner.join(this.getCommandPermissionMessage()));
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
        return new String[]{"You do not have permission to use this command."};
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
            sender.sendMessage(ChatColor.RED + "This command is not applicable to any users.");
        }

        if (!this.getCommandType().isSenderApplicable(sender))
        {
            sender.sendMessage(
                    ChatColor.RED + "This command is " + this.getCommandType().getApplicableSenderName() + "-only.");
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
            Method setName = Command.class.getDeclaredMethod("setName", String.class);
            setName.invoke(this, name);
            return true;
        }
        catch (ReflectiveOperationException e)
        {
            try
            {
                Field nameField = Command.class.getDeclaredField("name");
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(nameField, modifiersField.getInt(nameField) & 16);
                nameField.setAccessible(true);
                nameField.set(this, name);
                return true;
            }
            catch (ReflectiveOperationException e2)
            {
                this.plugin.getLogger().warning("Unable to set command name:");
                this.plugin.getLogger().warning(e2.getClass().getCanonicalName() + ": " + e2.getMessage());
                return false;
            }
        }
    }
}
