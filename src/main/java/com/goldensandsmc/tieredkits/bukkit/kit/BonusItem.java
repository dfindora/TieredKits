package com.goldensandsmc.tieredkits.bukkit.kit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class BonusItem
{
    private final double probability;
    private ArrayList<ItemStack> items;

    public BonusItem(double probability, ArrayList<ItemStack> items)
    {
        this.probability = probability;
        this.items = items;
    }

    public double getProbability()
    {
        return this.probability;
    }

    public ArrayList<ItemStack> getItems()
    {
        if (this.items == null)
        {
            this.items = new ArrayList<>();
        }
        return this.items;
    }
}
