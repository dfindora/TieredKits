package com.goldensandsmc.tieredkits.bukkit.kit;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class KitTier
{
    private int index;
    private int usesRequired;
    private boolean cascade;
    private LinkedList<ItemStack> items;
    private LinkedList<BonusItem> bonuses;

    public KitTier(int index, int usesRequired, boolean cascade, Collection<ItemStack> items, Collection<BonusItem> bonuses)
    {
        this.index = index;
        this.usesRequired = usesRequired;
        this.cascade = cascade;
        this.items = items == null ? null : new LinkedList<>(items);
        this.bonuses = bonuses == null ? null : new LinkedList<>(bonuses);
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public int getUsesRequired()
    {
        return this.usesRequired;
    }

    public void setUsesRequired(int usesRequired)
    {
        this.usesRequired = usesRequired;
    }

    public void setDoesCascade(boolean cascade)
    {
        this.cascade = cascade;
    }

    public boolean doesCascade()
    {
        return this.cascade;
    }

    public List<ItemStack> getItems()
    {
        if (this.items == null)
        {
            this.items = new LinkedList<>();
        }

        return this.items;
    }

    public List<BonusItem> getBonuses()
    {
        if (this.bonuses == null)
        {
            this.bonuses = new LinkedList<>();
        }

        return this.bonuses;
    }
}
