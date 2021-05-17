package com.goldensandsmc.tieredkits.bukkit.kit;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class KitTier {
    private int available_after;
    private boolean cascade;
    private LinkedList<ItemStack> items;
    private LinkedList<BonusItem> bonuses;

    public KitTier(int available_after, boolean cascade, Collection<ItemStack> items, Collection<BonusItem> bonuses) {
        this.available_after = available_after;
        this.cascade = cascade;
        this.items = items == null ? null : new LinkedList(items);
        this.bonuses = bonuses == null ? null : new LinkedList(bonuses);
    }

    public int getAvailableAfter() {
        return this.available_after;
    }

    public void setAvailableAfter(int available_after) {
        this.available_after = available_after;
    }

    public void setDoesCascade(boolean cascade) {
        this.cascade = cascade;
    }

    public boolean doesCascade() {
        return this.cascade;
    }

    public List<ItemStack> getItems() {
        if (this.items == null) {
            this.items = new LinkedList();
        }

        return this.items;
    }

    public List<BonusItem> getBonuses() {
        if (this.bonuses == null) {
            this.bonuses = new LinkedList();
        }

        return this.bonuses;
    }
}
