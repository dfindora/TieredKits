package com.goldensandsmc.tieredkits.sponge.kit;

import com.goldensandsmc.tieredkits.sponge.TieredKits;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

@ConfigSerializable
public class Kit
{
    @Setting(
            value = "cooldown",
            comment = "The cooldown, in seconds, before this kit can be used again"
    )
    private long cooldown;
    @Setting("tiers")
    private List<KitTier> tiers;

    private Kit()
    {
    }

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

    public List<KitTier> getTiers()
    {
        if (this.tiers == null)
        {
            this.tiers = new LinkedList<>();
        }

        return this.tiers;
    }

    public static boolean hasKitPermission(CommandSource source, String kit)
    {
        return !(source instanceof Player) || source.hasPermission("tieredkits.kit.*") || source
                .hasPermission("tieredkits.kit." + kit.toLowerCase());
    }

    public static Map<String, Kit> getAvailableKits(CommandSource player)
    {
        Map<String, Kit> kits = new HashMap<>(TieredKits.getInstance().getKits());
        if (player instanceof Player)
        {
            kits.entrySet().removeIf((entry) ->
                                             !hasKitPermission(player, entry.getKey()));
        }
        return kits;
    }

    public boolean canUseNow(CommandSource source, KitUsage usage)
    {
        return !(source instanceof Player) || source.hasPermission("tieredkits.exemptdelay")
               || usage.getLastUsed() + this.cooldown * 1000L <= System.currentTimeMillis();
    }
}
