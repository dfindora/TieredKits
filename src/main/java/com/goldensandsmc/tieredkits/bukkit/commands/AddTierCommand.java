package com.goldensandsmc.tieredkits.bukkit.commands;

import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.goldensandsmc.tieredkits.bukkit.kit.BonusItem;
import com.goldensandsmc.tieredkits.bukkit.kit.Kit;
import com.goldensandsmc.tieredkits.bukkit.kit.KitTier;
import com.google.common.collect.Lists;
import com.shortcircuit.utils.bukkit.command.BaseCommand;
import com.shortcircuit.utils.bukkit.command.CommandType;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.bukkit.command.exceptions.InvalidArgumentException;
import com.shortcircuit.utils.bukkit.command.exceptions.TooFewArgumentsException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;

import java.util.LinkedList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AddTierCommand extends BaseCommand<Player>
{
    public AddTierCommand(TieredKits plugin)
    {
        super(plugin);
    }

    public String getCommandName()
    {
        return "addtier";
    }

    public String[] getCommandAliases()
    {
        return new String[]{"atier"};
    }

    public CommandType getCommandType()
    {
        return CommandType.PLAYER;
    }

    public String[] getCommandDescription()
    {
        return new String[]{"add a new tier to an existing kit."};
    }

    public String[] getCommandUsage()
    {
        return new String[]{"/" + getCommandName() + " <name> <index> [<uses> <cascade:true|false>]"};
    }

    public String getCommandPermission()
    {
        return "essentials.kit.tier.add";
    }

    public String[] exec(Player sender, String command, ConcurrentArrayList<String> args) throws CommandException
    {
        TieredKits plugin = this.getPlugin();
        if (args.size() < 2)
        {
            throw new TooFewArgumentsException(getCommandUsage()[0]);
        }
        else
        {
            String name = args.remove(0).toLowerCase();
            if (plugin.getKits().containsKey(name))
            {
                Kit kit = plugin.getKits().get(name);
                if (args.size() < 3)
                {
                    throw new TooFewArgumentsException(getCommandUsage()[0]);
                }
                else
                {
                    int index;
                    try
                    {
                        index = Integer.parseInt(args.get(0));
                    }
                    catch (NumberFormatException e)
                    {
                        throw new InvalidArgumentException("Unable to parse number: " + args.get(0));
                    }
                    if(kit.getTierIndexes().contains(index))
                    {
                        throw new InvalidArgumentException("This kit already contains a tier with that index.");
                    }

                    int uses;
                    try
                    {
                        uses = Integer.parseInt(args.get(1));
                    }
                    catch (NumberFormatException e)
                    {
                        throw new InvalidArgumentException("Unable to parse number: " + args.get(1));
                    }

                    boolean cascade = args.get(2).matches("t(rue)?|y(es)?|1");
                    PlayerInventory inventory = sender.getInventory();
                    LinkedList<ItemStack> items = new LinkedList<>();
                    LinkedList<BonusItem> bonusItems = new LinkedList<>();

                    for (int i = 0; i < 9; ++i)
                    {
                        ItemStack item = inventory.getItem(i);
                        if (item != null && !item.getType().name().equalsIgnoreCase("air"))
                        {
                            items.add(item.clone());
                        }
                    }

                    for (int i = 27; i < 36; ++i)
                    {
                        ItemStack item = inventory.getItem(i);
                        if (item != null && !item.getType().name().equalsIgnoreCase("air"))
                        {
                            items.add(item.clone());
                        }
                    }

                    for (int i = 9; i < 27; ++i)
                    {
                        ItemStack item = inventory.getItem(i);
                        if (item != null && !item.getType().name().equalsIgnoreCase("air"))
                        {
                            bonusItems
                                    .add(new BonusItem(0.15D, Lists.newArrayList(item.clone())));
                        }
                    }

                    KitTier tier = new KitTier(index, uses, cascade, items, bonusItems);
                    kit.getTiers().add(tier);
                    plugin.saveKits();
                    return new String[]{"Added tier."};
                }
            }
            else
            {
                throw new InvalidArgumentException("There is no kit with that name.");
            }
        }
    }
}
