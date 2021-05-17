package com.goldensandsmc.tieredkits.sponge.kit;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class KitUsage
{
    @Setting("total_uses")
    private int total_uses;
    @Setting("last_used")
    private long last_used;

    public KitUsage()
    {
    }

    public int getTotalUses()
    {
        return this.total_uses;
    }

    public void setTotalUses(int total_uses)
    {
        this.total_uses = total_uses;
    }

    public long getLastUsed()
    {
        return this.last_used;
    }

    public void setLastUsed(long last_used)
    {
        this.last_used = last_used;
    }
}
