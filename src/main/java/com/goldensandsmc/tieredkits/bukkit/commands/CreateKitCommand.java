package com.goldensandsmc.tieredkits.bukkit.commands;

import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.goldensandsmc.tieredkits.bukkit.kit.Kit;
import com.shortcircuit.utils.bukkit.command.BaseCommand;
import com.shortcircuit.utils.bukkit.command.CommandType;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.bukkit.command.exceptions.InvalidArgumentException;
import com.shortcircuit.utils.bukkit.command.exceptions.TooFewArgumentsException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CreateKitCommand extends BaseCommand<CommandSender>
{
    public CreateKitCommand(TieredKits plugin)
    {
        super(plugin);
    }

    @Override
    public String getCommandName()
    {
        return "createkit";
    }

    @Override
    public String[] getCommandAliases()
    {
        return new String[]{"ckit"};
    }

    @Override
    public CommandType getCommandType()
    {
        return CommandType.ANY;
    }

    @Override
    public String[] getCommandDescription()
    {
        return new String[]{"Creates an empty kit with the specified name."};
    }

    @Override
    public String[] getCommandUsage()
    {
        return new String[]{"/" + getCommandName() + " <name>"};
    }

    @Override
    public String getCommandPermission()
    {
        return "essentials.kit.create";
    }

    @Override
    public String[] exec(CommandSender sender, String command, ConcurrentArrayList<String> args)
            throws CommandException
    {
        TieredKits plugin = this.getPlugin();
        if (args.size() == 1)
        {
            String name = args.remove(0).toLowerCase();
            Kit kit;
            if (plugin.getKits().containsKey(name))
            {
                throw new InvalidArgumentException("A kit with that name already exists.");
            }
            else
            {
                kit = new Kit(0L, null);
                plugin.getKits().put(name, kit);
                plugin.saveKits();
                return new String[]{"Created an empty kit. Use " + ChatColor.GOLD + "/editkit " + name
                                    + ChatColor.AQUA + " to edit this kit."};
            }
        }
        else
        {
            throw new TooFewArgumentsException("Too few arguments. Usage: " + getCommandUsage()[0]);
        }
    }
}
