package com.goldensandsmc.tieredkits.bukkit.commands;

import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.shortcircuit.utils.bukkit.command.BaseCommand;
import com.shortcircuit.utils.bukkit.command.CommandType;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

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
        return "essentials.kit.reload";
    }

    @Override
    public String[] exec(CommandSender sender, String command, ConcurrentArrayList<String> list)
            throws CommandException
    {
        boolean kitsLoaded = this.getPlugin().loadKits();
        boolean usagesNull = this.getPlugin().getKitUsage() == null || this.getPlugin().getKitUsage().isEmpty();

        ArrayList<String> messages = new ArrayList<>();
        if (kitsLoaded)
        {
            messages.add("Kits reloaded from file.");
        }
        else
        {
            throw new CommandException("Kit reload failed. Check logs.");
        }

        if(usagesNull)
        {
            boolean usagesLoaded = this.getPlugin().loadUsages();
            if(usagesLoaded)
            {
                messages.add("Usages were empty, so they were reloaded as a precaution.");
            }
            else
            {
                throw new CommandException("Attempted to reload usages, but it failed. Check logs.");
            }
        }

        return messages.toArray(new String[0]);
    }
}
