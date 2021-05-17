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
        return new String[]{"/<command> [kit] [player]"};
    }

    public String getCommandPermission()
    {
        return "essentials.kit";
    }

    public String[] exec(Player sender, String command, ConcurrentArrayList<String> args) throws CommandException
    {
        Player target = sender;
        long now = System.currentTimeMillis();
        LinkedList<String> availableKits = new LinkedList<>();

        for(Entry<String, Kit> kitEntry : this.getPlugin().getKits().entrySet())
        {
            if (sender.hasPermission("essentials.kits.*") || sender
                    .hasPermission("essentials.kits." + kitEntry.getKey()))
            {
                if (args.isEmpty())
                {
                    return new String[]{
                            "Available kits: " + ChatColor.GOLD + Joiner.on(ChatColor.AQUA + ", " + ChatColor.GOLD)
                                                                        .join(availableKits)};
                }

                String kit_name = args.remove(0);
                if (!args.isEmpty())
                {
                    target = this.getPlugin().getServer().getPlayer(args.get(0));
                    if (target == null)
                    {
                        return new String[]{ChatColor.RED + "The specified player is not online"};
                    }
                }

                if (!availableKits.contains(kit_name))
                {
                    throw new CommandException("You may not use that kit right now!");
                }

                Kit kit = this.getPlugin().getKits().get(kit_name);
                KitUsage usage = new KitUsage();
                HashMap<String, KitUsage> kit_usage = this.getPlugin().getKitUsage().get(sender.getUniqueId());
                if (kit_usage != null && kit_usage.containsKey(kit_name))
                {
                    usage = kit_usage.get(kit_name);
                }

                List<KitTier> applicable_tiers = Utils.getApplicableTiers(kit, usage);
                LinkedList<ItemStack> allItems = new LinkedList<>();

                for(KitTier tier : applicable_tiers)
                {
                    for (ItemStack item : tier.getItems())
                    {
                        allItems.add(item.clone());
                    }

                    Random random = new Random();
                    for(BonusItem bonus : tier.getBonuses())
                    {
                        if(random.nextDouble() < bonus.getProbability())
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

                int prev_tiers = applicable_tiers.size();
                usage.setTotalUses(usage.getTotalUses() + 1L);
                usage.setLastUsed(now);
                int new_tiers = Utils.getApplicableTiers(kit, usage).size();
                if (new_tiers > prev_tiers)
                {
                    sender.sendMessage(
                            ChatColor.AQUA + "Congratulations! Your " + ChatColor.GOLD + kit_name + ChatColor.AQUA
                            + " kit has upgraded!");
                }

                if (this.getPlugin().getKitUsage().containsKey(sender.getUniqueId()))
                {
                    this.getPlugin().getKitUsage().get(sender.getUniqueId()).put(kit_name, usage);
                }
                else
                {
                    HashMap<String, KitUsage> bleh = new HashMap<>();
                    bleh.put(kit_name, usage);
                    this.getPlugin().getKitUsage().put(sender.getUniqueId(), bleh);
                }

                return new String[]{"Enjoy your kit!"};
            }

            boolean can_use_now = sender.hasPermission("essentials.kit.exemptdelay");
            if (!can_use_now)
            {
                HashMap<UUID, HashMap<String, KitUsage>> kit_usage = this.getPlugin().getKitUsage();
                if (!kit_usage.containsKey(sender.getUniqueId()))
                {
                    can_use_now = true;
                }
                else
                {
                    HashMap<String, KitUsage> player_usage = kit_usage.get(sender.getUniqueId());
                    if (!player_usage.containsKey(kitEntry.getKey()))
                    {
                        can_use_now = true;
                    }
                    else
                    {
                        KitUsage usage = player_usage.get(kitEntry.getKey());
                        long cooldown_millis = kitEntry.getValue().getCooldownSeconds() * 1000L;
                        if (cooldown_millis < 0L)
                        {
                            can_use_now = false;
                        }
                        else
                        {
                            can_use_now = now - usage.getLastUsed() >= cooldown_millis;
                        }
                    }
                }
            }

            if (can_use_now)
            {
                availableKits.add(kitEntry.getKey());
            }
            else
            {
                availableKits.add(ChatColor.STRIKETHROUGH + kitEntry.getKey() + ChatColor.RESET);
            }
        }
        return new String[]{"null"};
    }
}
