package com.goldensandsmc.tieredkits.bukkit.kit;

public class KitUsage
{
    private long totalUses;
    private long lastUsed;

    public KitUsage()
    {
    }

    public long getTotalUses()
    {
        return this.totalUses;
    }

    public void setTotalUses(long totalUses)
    {
        this.totalUses = totalUses;
    }

    public long getLastUsed()
    {
        return this.lastUsed;
    }

    public void setLastUsed(long lastUsed)
    {
        this.lastUsed = lastUsed;
    }
}
