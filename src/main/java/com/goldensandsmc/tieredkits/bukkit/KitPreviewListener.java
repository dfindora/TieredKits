package com.goldensandsmc.tieredkits.bukkit;

import com.goldensandsmc.tieredkits.bukkit.kit.BonusItem;
import com.goldensandsmc.tieredkits.bukkit.kit.KitTier;
import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitPreviewListener implements Listener
{
    private static final HashMap<UUID, Inventory> preview_inventories = new HashMap<>();
    private static final ItemStack barrier;

    public KitPreviewListener()
    {
    }

    public static void openKitPreview(Player player, String kit, List<KitTier> tiers)
    {
        int numRows = 0;
        int bonus_items = 0;
        for(KitTier tier : tiers)
        {
            numRows += (int) (Math.ceil(tier.getItems().size() / 9.0));
            for(BonusItem bonus : tier.getBonuses())
            {
                bonus_items += bonus.getItems().size();
            }
            numRows += (int) (Math.ceil(bonus_items / 9.0));
        }

        if (tiers.size() > 1)
        {
            numRows += tiers.size() - 1;
        }

        if (numRows > 14)
        {
            player.sendMessage(ChatColor.RED + "There are too many items to show at once. Please use " + ChatColor.GOLD
                               + "/previewkit " + kit + "<uses>" + ChatColor.RED + ".");
        }
        else
        {
            Inventory inventory = Bukkit.getServer().createInventory(player, numRows * 9, "Previewing kit: "
                                                                                          + kit);
            for (int row = 0; row < tiers.size(); ++row)
            {
                KitTier tier = tiers.get(row);
                for (int column = 0; column < tier.getItems().size(); ++column)
                {
                    ItemStack item = tier.getItems().get(column);
                    if (column == 9)
                    {
                        ++row;
                        column = 0;
                    }

                    int index = row * 9 + column;
                    item = item.clone();
                    ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getServer().getItemFactory()
                                                                                    .getItemMeta(item.getType());
                    meta.setLore(ImmutableList.of("Available after " + tier.getAvailableAfter() + " uses",
                                                  "This item is always given",
                                                  "This tier does" + (tier.doesCascade() ? "" : " not") + " cascade"));
                    item.setItemMeta(meta);
                    inventory.setItem(index, item);
                }

                ++row;
                for(BonusItem bonus : tier.getBonuses())
                {
                    for (int column = 0; column < bonus.getItems().size(); ++column)
                    {
                        ItemStack item = bonus.getItems().get(column);
                        if (column == 9)
                        {
                            ++row;
                            column = 0;
                        }

                        int index = row * 9 + column;
                        ItemStack clone = item.clone();
                        ItemMeta meta = clone.hasItemMeta() ? clone.getItemMeta() : Bukkit.getServer().getItemFactory()
                                                                                          .getItemMeta(clone.getType());
                        meta.setLore(ImmutableList.of("Available after " + tier.getAvailableAfter() + " uses",
                                                      bonus.getProbability() * 100.0D + "% chance",
                                                      "This tier does" + (tier.doesCascade() ? "" : " not")
                                                      + " cascade"));
                        clone.setItemMeta(meta);
                        inventory.setItem(index, clone);
                    }
                }

                ++row;

                try
                {
                    for (int column = 0; column < 9; ++column)
                    {
                        inventory.setItem(row * 9 + column, barrier.clone());
                    }
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();
                }
            }

            player.openInventory(inventory);
            preview_inventories.put(player.getUniqueId(), inventory);
        }
    }

    @EventHandler
    public void cancelEdit(InventoryClickEvent event)
    {
        HumanEntity player = event.getWhoClicked();
        if (preview_inventories.containsKey(player.getUniqueId()))
        {
            Inventory inventory = preview_inventories.get(player.getUniqueId());
            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT
                || event.getClick() == ClickType.DOUBLE_CLICK || inventory.equals(event.getClickedInventory()))
            {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void closePreview(PlayerQuitEvent event)
    {
        HumanEntity player = event.getPlayer();
        preview_inventories.remove(player.getUniqueId());
    }

    @EventHandler
    public void closePreview(PlayerKickEvent event)
    {
        HumanEntity player = event.getPlayer();
        preview_inventories.remove(player.getUniqueId());
    }

    @EventHandler
    public void closePreview(InventoryCloseEvent event)
    {
        HumanEntity player = event.getPlayer();
        if (preview_inventories.containsKey(player.getUniqueId()))
        {
            Inventory inventory = preview_inventories.get(player.getUniqueId());
            if (inventory.equals(event.getInventory()))
            {
                preview_inventories.remove(player.getUniqueId());
            }

        }
    }

    static
    {
        barrier = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta meta = Bukkit.getServer().getItemFactory().getItemMeta(barrier.getType());
        meta.setDisplayName(ChatColor.BLACK + "");
        barrier.setItemMeta(meta);
    }
}
