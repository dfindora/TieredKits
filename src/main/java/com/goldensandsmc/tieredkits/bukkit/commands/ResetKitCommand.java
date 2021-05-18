package com.goldensandsmc.tieredkits.bukkit.commands;

import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.goldensandsmc.tieredkits.bukkit.kit.KitUsage;
import com.shortcircuit.utils.bukkit.command.BaseCommand;
import com.shortcircuit.utils.bukkit.command.CommandType;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.bukkit.command.exceptions.InvalidArgumentException;
import com.shortcircuit.utils.bukkit.command.exceptions.TooFewArgumentsException;
import com.shortcircuit.utils.bukkit.command.exceptions.TooManyArgumentsException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ResetKitCommand extends BaseCommand<CommandSender>
{
    public ResetKitCommand(TieredKits plugin)
    {
        super(plugin);
    }

    @Override
    public String getCommandName()
    {
        return "resetkit";
    }

    @Override
    public String[] getCommandAliases()
    {
        return new String[]{"rkit"};
    }

    @Override
    public CommandType getCommandType()
    {
        return CommandType.ANY;
    }

    @Override
    public String[] getCommandDescription()
    {
        return new String[]{"Edit a user's kit data."};
    }

    @Override
    public String[] getCommandUsage()
    {
        return new String[]{"/" + getCommandName() + " <reset|usage|lastused> <user> <kit> [<value>]"};
    }

    @Override
    public String getCommandPermission()
    {
        return "essentials.kit.reset";
    }

    @Override
    public String[] exec(CommandSender sender, String command, ConcurrentArrayList<String> args)
            throws CommandException
    {
        if(args.size() < 3)
        {
            throw new TooFewArgumentsException("Too few arguments. Usage: " + getCommandUsage()[0]);
        }
        String type = args.remove(0);
        Player player = this.getPlugin().getServer().getPlayer(args.remove(0));
        UUID uuid;
        if(player != null)
        {
            uuid = player.getUniqueId();
        }
        else
        {
            throw new CommandException("Target player is not online.");
        }

        String kitName = args.remove(0);
        KitUsage kitUsage;
        if (this.getPlugin().getKitUsage().get(uuid).containsKey(kitName))
        {
            kitUsage = this.getPlugin().getKitUsage().get(uuid).get(kitName);
        }
        else
        {
            throw new InvalidArgumentException("Kit not found.");
        }
        switch (type)
        {
            case "reset":
                if(args.size() == 0)
                {
                    kitUsage.setLastUsed(0L);
                    kitUsage.setTotalUses(0L);
                    break;
                }
                else
                {
                    throw new TooManyArgumentsException("Too many arguments. Usage: " + getCommandUsage()[0]);
                }
            case "usage":
                if(args.size() == 1)
                {
                    kitUsage.setTotalUses(Long.parseLong(args.remove(0)));
                    break;
                }
                else if(args.size() == 0)
                {
                    throw new TooFewArgumentsException("Too few arguments. Usage: " + getCommandUsage()[0]);
                }
                else
                {
                    throw new TooManyArgumentsException("Too many arguments. Usage: " + getCommandUsage()[0]);
                }
            case "lastused":
                if(args.size() == 1)
                {
                    kitUsage.setLastUsed(Long.parseLong(args.remove(0)));
                    break;
                }
                else if(args.size() == 0)
                {
                    throw new TooFewArgumentsException("Too few arguments. Usage: " + getCommandUsage()[0]);
                }
                else
                {
                    throw new TooManyArgumentsException("Too many arguments. Usage: " + getCommandUsage()[0]);
                }
            default:
                throw new InvalidArgumentException("invalid type. Expected <reset|usage|lastused>");
        }
        HashMap<String, KitUsage> map = this.getPlugin().getKitUsage().get(uuid);
        map.put(kitName, kitUsage);
        this.getPlugin().getKitUsage().put(uuid, map);
        this.getPlugin().saveUsages();
        this.getPlugin().loadUsages();
        return new String[]{"User " + player.getName() + " kits updated."};
    }
}
