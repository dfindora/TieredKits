package com.goldensandsmc.tieredkits.bukkit.commands;

import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.shortcircuit.utils.bukkit.command.BaseCommand;
import com.shortcircuit.utils.bukkit.command.CommandType;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.bukkit.command.exceptions.InvalidArgumentException;
import com.shortcircuit.utils.bukkit.command.exceptions.TooFewArgumentsException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;
import org.bukkit.command.CommandSender;

public class RemoveKitCommand extends BaseCommand<CommandSender>
{
    public RemoveKitCommand(TieredKits plugin)
    {
        super(plugin);
    }

    @Override
    public String getCommandName()
    {
        return "removekit";
    }

    @Override
    public String[] getCommandAliases()
    {
        return new String[]{"deletekit"};
    }

    @Override
    public CommandType getCommandType()
    {
        return CommandType.ANY;
    }

    @Override
    public String[] getCommandDescription()
    {
        return new String[]{"Deletes the specified kit."};
    }

    @Override
    public String[] getCommandUsage()
    {
        return new String[]{"/" + getCommandName() + " <name>"};
    }

    @Override
    public String getCommandPermission()
    {
        return "essentials.kit.remove";
    }

    @Override
    public String[] exec(CommandSender sender, String command, ConcurrentArrayList<String> args) throws CommandException
    {
        TieredKits plugin = this.getPlugin();
        if(args.size() == 1)
        {
            String name = args.remove(0);
            if (plugin.getKits().containsKey(name))
            {
                plugin.getKits().remove(name);
                plugin.saveKits();
                return new String[]{"Removed kit."};
            }
            else
            {
                throw new InvalidArgumentException("There is no kit with that name.");
            }
        }
        else
        {
            throw new TooFewArgumentsException(getCommandUsage()[0]);
        }
    }
}
