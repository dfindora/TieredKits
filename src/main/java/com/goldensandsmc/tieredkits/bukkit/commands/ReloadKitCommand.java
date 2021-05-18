package com.goldensandsmc.tieredkits.bukkit.commands;

import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.shortcircuit.utils.bukkit.command.BaseCommand;
import com.shortcircuit.utils.bukkit.command.CommandType;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;
import org.bukkit.command.CommandSender;

public class ReloadKitCommand extends BaseCommand<CommandSender>
{
    public ReloadKitCommand(TieredKits plugin)
    {
        super(plugin);
    }

    @Override
    public String getCommandName()
    {
        return "reloadkits";
    }

    @Override
    public String[] getCommandAliases()
    {
        return new String[0];
    }

    @Override
    public CommandType getCommandType()
    {
        return CommandType.ANY;
    }

    @Override
    public String[] getCommandDescription()
    {
        return new String[]{"Reloads kits from configs."};
    }

    @Override
    public String[] getCommandUsage()
    {
        return new String[]{"/" + getCommandName()};
    }

    @Override
    public String getCommandPermission()
    {
        return "essentials.kits.reload";
    }

    @Override
    public String[] exec(CommandSender sender, String command, ConcurrentArrayList<String> list)
            throws CommandException
    {
        this.getPlugin().loadKits();
        return new String[]{"Kits reloaded from file"};
    }
}
