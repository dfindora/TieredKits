package com.goldensandsmc.tieredkits.bukkit.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Kit
{
    private long cooldown;
    private LinkedList<KitTier> tiers;

    public Kit(long cooldown, Collection<KitTier> tiers)
    {
        this.cooldown = cooldown;
        this.tiers = tiers == null ? null : new LinkedList<>(tiers);
    }

    public void setCooldownSeconds(long seconds)
    {
        this.cooldown = seconds;
    }

    public long getCooldownSeconds()
    {
        return this.cooldown;
    }

    public LinkedList<KitTier> getTiers()
    {
        if (this.tiers == null)
        {
            this.tiers = new LinkedList<>();
        }

        return this.tiers;
    }

    public List<Integer> getTierIndexes()
    {
        List<Integer> indexes = new ArrayList<>();
        for(KitTier tier : tiers)
        {
            indexes.add(tier.getIndex());
        }
        return indexes;
    }

    public void removeTierByIndex(int index)
    {
        for(KitTier tier : tiers)
        {
            if(tier.getIndex() == index)
            {
                tiers.remove(tier);
                break;
            }
        }
    }
}
