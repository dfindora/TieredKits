package com.goldensandsmc.tieredkits.bukkit.kit;

public class KitUsage {
    private long total_uses;
    private long last_used;

    public KitUsage() {
    }

    public long getTotalUses() {
        return this.total_uses;
    }

    public void setTotalUses(long total_uses) {
        this.total_uses = total_uses;
    }

    public long getLastUsed() {
        return this.last_used;
    }

    public void setLastUsed(long last_used) {
        this.last_used = last_used;
    }
}
