package com.goldensandsmc.tieredkits.bukkit.commands;

import com.goldensandsmc.tieredkits.bukkit.TieredKits;
import com.goldensandsmc.tieredkits.bukkit.Utils;
import com.goldensandsmc.tieredkits.bukkit.kit.BonusItem;
import com.goldensandsmc.tieredkits.bukkit.kit.Kit;
import com.goldensandsmc.tieredkits.bukkit.kit.KitTier;
import com.goldensandsmc.tieredkits.bukkit.kit.KitUsage;
import com.google.common.base.Joiner;
import com.shortcircuit.utils.bukkit.command.BaseCommand;
import com.shortcircuit.utils.bukkit.command.CommandType;
import com.shortcircuit.utils.bukkit.command.exceptions.CommandException;
import com.shortcircuit.utils.collect.ConcurrentArrayList;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitCommand extends BaseCommand<Player>
{
    public KitCommand(TieredKits plugin)
    {
        super(plugin);
    }

    public String getCommandName()
    {
        return "kit";
    }

    public String[] getCommandAliases()
    {
        return new String[]{"kits"};
    }

    public CommandType getCommandType()
    {
        return CommandType.PLAYER;
    }

    public String[] getCommandDescription()
    {
        return new String[]{"Spawn a predefined kit"};
    }

    public String[] getCommandUsage()
    {
        return new String[]{"/" + getCommandName() + " [kit] [player]"};
    }

    public String getCommandPermission()
    {
        return "essentials.kit";
    }

    public String[] exec(Player sender, String command, ConcurrentArrayList<String> args) throws CommandException
    {
        long now = System.currentTimeMillis();
        LinkedList<String> availableKits = new LinkedList<>();

        for (Entry<String, Kit> kitEntry : this.getPlugin().getKits().entrySet())
        {
            if (sender.hasPermission("essentials.kits.*") || sender
                    .hasPermission("essentials.kits." + kitEntry.getKey()))
            {
                boolean canUseNow = sender.hasPermission("essentials.kit.exemptdelay");
                if (!canUseNow)
                {
                    HashMap<UUID, HashMap<String, KitUsage>> kitUsages = this.getPlugin().getKitUsage();
                    if (!kitUsages.containsKey(sender.getUniqueId()))
                    {
                        canUseNow = true;
                    }
                    else
                    {
                        HashMap<String, KitUsage> playerUsage = kitUsages.get(sender.getUniqueId());
                        if (!playerUsage.containsKey(kitEntry.getKey()))
                        {
                            canUseNow = true;
                        }
                        else
                        {
                            KitUsage canUseUsage = playerUsage.get(kitEntry.getKey());
                            long cooldownMillis = kitEntry.getValue().getCooldownSeconds() * 1000L;
                            if (cooldownMillis < 0L)
                            {
                                canUseNow = false;
                            }
                            else
                            {
                                canUseNow = now - canUseUsage.getLastUsed() >= cooldownMillis;
                            }
                        }
                    }
                }

                if (canUseNow)
                {
                    availableKits.add(kitEntry.getKey());
                }
                else
                {
                    availableKits.add(ChatColor.STRIKETHROUGH + kitEntry.getKey() + ChatColor.RESET);
                }
            }
        }
        if (args.isEmpty())
        {
            return new String[]{
                    "Available kits: " + ChatColor.GOLD + Joiner.on(ChatColor.AQUA + ", " + ChatColor.GOLD)
                                                                .join(availableKits)};
        }

        Player target = sender;
        String kitName = args.remove(0);
        if (!args.isEmpty())
        {
            target = this.getPlugin().getServer().getPlayer(args.get(0));
            if (target == null)
            {
                return new String[]{ChatColor.RED + "The specified player is not online"};
            }
        }

        if (!availableKits.contains(kitName))
        {
            throw new CommandException("You may not use that kit right now!");
        }

        Kit kit = this.getPlugin().getKits().get(kitName);
        KitUsage usage = new KitUsage();
        HashMap<String, KitUsage> kitUsage = this.getPlugin().getKitUsage().get(sender.getUniqueId());
        if (kitUsage != null && kitUsage.containsKey(kitName))
        {
            usage = kitUsage.get(kitName);
        }

        KitTier maxTier = Utils.getMaxTier(kit, usage);
        List<KitTier> applicableTiers = Utils.getApplicableTiers(kit, usage);
        LinkedList<ItemStack> allItems = new LinkedList<>();

        for (KitTier tier : applicableTiers)
        {
            for (ItemStack item : tier.getItems())
            {
                allItems.add(item.clone());
            }

            Random random = new Random();
            for (BonusItem bonus : tier.getBonuses())
            {
                if (random.nextDouble() < bonus.getProbability())
                {
                    for (ItemStack item : bonus.getItems())
                    {
                        allItems.add(item.clone());
                    }
                }
            }
        }

        for (ItemStack overflow : target.getInventory().addItem(allItems.toArray(new ItemStack[0])).values())
        {
            target.getWorld().dropItem(target.getLocation().add(0.0, 0.25, 0.0), overflow);
        }

        int prevTier = maxTier.getUsesRequired();
        usage.setTotalUses(usage.getTotalUses() + 1L);
        usage.setLastUsed(now);
        int newTier = Utils.getMaxTier(kit, usage).getUsesRequired();
        if (prevTier < newTier)
        {
            sender.sendMessage(
                    ChatColor.AQUA + "Congratulations! Your " + ChatColor.GOLD + kitName + ChatColor.AQUA
                    + " kit has upgraded!");
        }

        if (this.getPlugin().getKitUsage().containsKey(sender.getUniqueId()))
        {
            this.getPlugin().getKitUsage().get(sender.getUniqueId()).put(kitName, usage);
        }
        else
        {
            HashMap<String, KitUsage> bleh = new HashMap<>();
            bleh.put(kitName, usage);
            this.getPlugin().getKitUsage().put(sender.getUniqueId(), bleh);
        }
        return new String[]{"Enjoy your kit!"};
        //return new String[]{"kits are empty or specified kit wasn't found."};
    }
}
