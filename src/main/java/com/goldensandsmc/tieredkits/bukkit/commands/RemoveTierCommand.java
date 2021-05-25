package com.goldensandsmc.tieredkits.bukkit.commands;

import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.goldensandsmc.tieredkits.bukkit.kit.Kit;
import com.shortcircuit.utils.bukkit.command.BaseCommand;
import com.shortcircuit.utils.bukkit.command.CommandType;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.bukkit.command.exceptions.InvalidArgumentException;
import com.shortcircuit.utils.bukkit.command.exceptions.TooFewArgumentsException;
import com.shortcircuit.utils.bukkit.command.exceptions.TooManyArgumentsException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;
import org.bukkit.command.CommandSender;

public class RemoveTierCommand extends BaseCommand<CommandSender>
{
    public RemoveTierCommand(TieredKits plugin)
    {
        super(plugin);
    }

    @Override
    public String getCommandName()
    {
        return "removetier";
    }

    @Override
    public String[] getCommandAliases()
    {
        return new String[]{"rtier"};
    }

    @Override
    public CommandType getCommandType()
    {
        return CommandType.ANY;
    }

    @Override
    public String[] getCommandDescription()
    {
        return new String[]{"removes the target tier."};
    }

    @Override
    public String[] getCommandUsage()
    {
        return new String[]{"/" + getCommandName() + " <name> <index>"};
    }

    @Override
    public String getCommandPermission()
    {
        return "essentials.kit.tier.remove";
    }

    @Override
    public String[] exec(CommandSender sender, String command, ConcurrentArrayList<String> args) throws CommandException
    {
        if(args.size() < 2)
        {
            throw new TooFewArgumentsException(getCommandUsage()[0]);
        }
        else if(args.size() > 2)
        {
            throw new TooManyArgumentsException(getCommandUsage()[0]);
        }
        String name = args.remove(0);
        int index;
        if(!getPlugin().getKits().containsKey(name))
        {
            throw new InvalidArgumentException("Kit " + name + " not found.");
        }
        else
        {
            Kit kit = getPlugin().getKits().get(name);
            try
            {
                index = Integer.parseInt(args.get(0));
            }
            catch (NumberFormatException e)
            {
                throw new InvalidArgumentException("Unable to parse number: " + args.get(0));
            }
            if(!kit.getTierIndexes().contains(index))
            {
                throw new InvalidArgumentException("Kit " + name + " does not contain a tier with the index of "
                                                   + index + ".");
            }
            else
            {
                kit.removeTierByIndex(index);
                getPlugin().saveKits();
            }
        }
        return new String[]{"Removed tier with index " + index + " from kit " + name + "."};
    }
}
