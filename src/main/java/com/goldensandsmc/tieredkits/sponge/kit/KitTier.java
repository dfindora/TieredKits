package com.goldensandsmc.tieredkits.sponge.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.inventory.ItemStack;

@ConfigSerializable
public class KitTier
{
    @Setting(
            value = "available_after",
            comment = "The number of uses before this tier becomes available"
    )
    private int available_after;
    @Setting(
            value = "cascade",
            comment = "If true, this tier will be included in all higher tiers"
    )
    private boolean cascade;
    @Setting("items")
    private List<ItemStack> items;
    @Setting("bonuses")
    private List<BonusItem> bonuses;

    private KitTier()
    {
    }

    public KitTier(int available_after, boolean cascade, Collection<ItemStack> items, Collection<BonusItem> bonuses)
    {
        this.available_after = available_after;
        this.cascade = cascade;
        this.items = items == null ? null : new ArrayList<>(items);
        this.bonuses = bonuses == null ? null : new ArrayList<>(bonuses);
    }

    public int getAvailableAfter()
    {
        return this.available_after;
    }

    public void setAvailableAfter(int available_after)
    {
        this.available_after = available_after;
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

    public static List<KitTier> getApplicableTiers(Kit kit, KitUsage usage)
    {
        LinkedList<KitTier> applicableTiers = new LinkedList<>();
        KitTier maxTier = null;
        Iterator var4 = kit.getTiers().iterator();

        while (true)
        {
            KitTier tier;
            do
            {
                do
                {
                    if (!var4.hasNext())
                    {
                        if (maxTier != null)
                        {
                            var4 = kit.getTiers().iterator();

                            while (true)
                            {
                                do
                                {
                                    if (!var4.hasNext())
                                    {
                                        return applicableTiers;
                                    }

                                    tier = (KitTier) var4.next();
                                } while ((tier.getAvailableAfter() >= maxTier.getAvailableAfter() || !tier
                                        .doesCascade()) && tier.getAvailableAfter() != maxTier.getAvailableAfter());

                                applicableTiers.add(tier);
                            }
                        }

                        return applicableTiers;
                    }

                    tier = (KitTier) var4.next();
                } while (tier.getAvailableAfter() > usage.getTotalUses());
            } while (maxTier != null && maxTier.getAvailableAfter() > tier.getAvailableAfter());

            maxTier = tier;
        }
    }
}
