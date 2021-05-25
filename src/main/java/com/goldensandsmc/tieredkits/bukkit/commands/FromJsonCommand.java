package com.goldensandsmc.tieredkits.bukkit.commands;

import com.goldensandsmc.tieredkits.JsonUtils;
import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.google.common.base.Joiner;
import com.shortcircuit.utils.bukkit.command.BaseCommand;
import com.shortcircuit.utils.bukkit.command.CommandType;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.bukkit.command.exceptions.TooFewArgumentsException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FromJsonCommand extends BaseCommand<CommandSender>
{
    public FromJsonCommand(TieredKits plugin)
    {
        super(plugin);
    }

    public String getCommandName()
    {
        return "fromjson";
    }

    public String[] getCommandAliases()
    {
        return new String[]{"itemfromjson", "jsontoitem", "toitem"};
    }

    public CommandType getCommandType()
    {
        return CommandType.ANY;
    }

    public String[] getCommandDescription()
    {
        return new String[]{"Converts a valid JSON string into an item"};
    }

    public String[] getCommandUsage()
    {
        return new String[]{"/" + getCommandName() + " [player] <json>"};
    }

    public String getCommandPermission()
    {
        return "sandcastle.foundation.command.fromjson";
    }

    public String[] exec(CommandSender sender, String command, ConcurrentArrayList<String> args) throws CommandException
    {
        if (args.isEmpty())
        {
            throw new TooFewArgumentsException(getCommandUsage()[0]);
        }
        else
        {
            Player target = this.getPlugin().getServer().getPlayer(args.remove(0));
            if (target == null)
            {
                return new String[]{"The specified player is not online."};
            }
            else
            {
                String rawJson = Joiner.on(' ').join(args);

                try
                {
                    ItemStack item = JsonUtils.fromJson(rawJson, ItemStack.class);

                    for (ItemStack overflow : target.getPlayer().getInventory().addItem(new ItemStack[]{item})
                                                    .values())
                    {
                        target.getPlayer().getWorld()
                              .dropItem(target.getPlayer().getLocation().add(0.0D, 0.25D, 0.0D), overflow);
                    }

                    return new String[]{"Successfully converted item."};
                }
                catch (Exception e)
                {
                    throw new CommandException(e);
                }
            }
        }
    }
}
