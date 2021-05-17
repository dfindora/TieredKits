package com.goldensandsmc.tieredkits.sponge.kit;

import java.util.LinkedList;
import java.util.List;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.inventory.ItemStack;

@ConfigSerializable
public class BonusItem
{
    @Setting(
            value = "probability",
            comment = "The chance, between 0 and 1, that this bonus will be included"
    )
    private double probability = 0.15D;
    @Setting("items")
    private List<ItemStack> items;

    private BonusItem()
    {
    }

    public BonusItem(double probability, List<ItemStack> items)
    {
        this.probability = probability;
        this.items = items == null ? null : new LinkedList<>(items);
    }

    public double getProbability()
    {
        return this.probability;
    }

    public List<ItemStack> getItems()
    {
        if (this.items == null)
        {
            this.items = new LinkedList<>();
        }

        return this.items;
    }
}
