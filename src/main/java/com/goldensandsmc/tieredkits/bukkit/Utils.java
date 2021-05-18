package com.goldensandsmc.tieredkits.bukkit;

import com.goldensandsmc.tieredkits.bukkit.kit.Kit;
import com.goldensandsmc.tieredkits.bukkit.kit.KitTier;
import com.goldensandsmc.tieredkits.bukkit.kit.KitUsage;
import com.google.common.collect.ImmutableMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.DyeColor;

public class Utils
{
    private static final ImmutableMap<Object, Object> colorLookup =
            ImmutableMap.builder()
                 .put("WHITE", Color.WHITE)
                 .put("WHITE_DYE", DyeColor.WHITE.getColor())
                 .put("WHITE_FIREWORK", DyeColor.WHITE.getFireworkColor())
                 .put("SILVER", Color.SILVER)
                 .put("SILVER_DYE", DyeColor.SILVER.getColor())
                 .put("SILVER_FIREWORK", DyeColor.SILVER.getFireworkColor())
                 .put("GRAY", Color.GRAY)
                 .put("GREY", Color.GRAY)
                 .put("GRAY_DYE", DyeColor.GRAY.getColor())
                 .put("GREY_DYE", DyeColor.GRAY.getColor())
                 .put("GRAY_FIREWORK", DyeColor.GRAY.getFireworkColor())
                 .put("GREY_FIREWORK", DyeColor.GRAY.getFireworkColor())
                 .put("BLACK", Color.BLACK)
                 .put("BLACK_DYE", DyeColor.BLACK.getColor())
                 .put("BLACK_FIREWORK", DyeColor.BLACK.getFireworkColor())
                 .put("RED", Color.RED)
                 .put("RED_DYE", DyeColor.RED.getColor())
                 .put("RED_FIREWORK", DyeColor.RED.getFireworkColor())
                 .put("MAROON", Color.MAROON)
                 .put("YELLOW", Color.YELLOW)
                 .put("YELLOW_DYE", DyeColor.YELLOW.getColor())
                 .put("YELLOW_FIREWORK", DyeColor.YELLOW.getFireworkColor())
                 .put("OLIVE", Color.OLIVE)
                 .put("LIME", Color.LIME)
                 .put("LIME_DYE", DyeColor.LIME.getColor())
                 .put("LIME_FIREWORK", DyeColor.LIME.getFireworkColor())
                 .put("GREEN", Color.GREEN)
                 .put("GREEN_DYE", DyeColor.GREEN.getColor())
                 .put("GREEN_FIREWORK", DyeColor.GREEN.getFireworkColor())
                 .put("AQUA", Color.AQUA)
                 .put("TEAL", Color.TEAL)
                 .put("BLUE", Color.BLUE)
                 .put("BLUE_DYE", DyeColor.BLUE.getColor())
                 .put("BLUE_FIREWORK", DyeColor.BLUE.getFireworkColor())
                 .put("NAVY", Color.NAVY)
                 .put("FUCHSIA", Color.FUCHSIA)
                 .put("PURPLE", Color.PURPLE)
                 .put("PURPLE_DYE", DyeColor.PURPLE.getColor())
                 .put("PURPLE_FIREWORK", DyeColor.PURPLE.getFireworkColor())
                 .put("ORANGE", Color.ORANGE)
                 .put("ORANGE_DYE", DyeColor.ORANGE.getColor())
                 .put("ORANGE_FIREWORK", DyeColor.ORANGE.getFireworkColor())
                 .put("PINK_DYE", DyeColor.PINK.getColor())
                 .put("PINK_FIREWORK", DyeColor.PINK.getFireworkColor())
                 .put("CYAN_DYE", DyeColor.CYAN.getColor())
                 .put("CYAN_FIREWORK", DyeColor.CYAN.getFireworkColor())
                 .put("BROWN_DYE", DyeColor.BROWN.getColor())
                 .put("BROWN_FIREWORK", DyeColor.BROWN.getFireworkColor())
                 .put("LIGHT_BLUE_DYE", DyeColor.LIGHT_BLUE.getColor())
                 .put("LIGHT_BLUE_FIREWORK", DyeColor.LIGHT_BLUE.getFireworkColor())
                 .put("MAGENTA_DYE", DyeColor.MAGENTA.getColor())
                 .put("MAGENTA_FIREWORK", DyeColor.MAGENTA.getFireworkColor())
                 .build();

    public Utils()
    {
    }

    public static Color getColorFromName(String name)
    {
        return (Color) colorLookup.get(name.toUpperCase());
    }

    public static String getNameFromColor(Color color)
    {
        for (Map.Entry<Object, Object> entry : colorLookup.entrySet())
        {
            if (entry.getValue().equals(color))
            {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    public static KitTier getMaxTier(Kit kit, KitUsage usage)
    {
        KitTier maxTier = new KitTier(0, false, null, null);
        for (KitTier tier : kit.getTiers())
        {
            boolean canUse = usage.getTotalUses() >= tier.getUsesRequired();
            boolean isHigherTier = tier.getUsesRequired() > maxTier.getUsesRequired();
            if (canUse && isHigherTier)
            {
                maxTier = tier;
            }
        }
        return maxTier;
    }

    public static List<KitTier> getApplicableTiers(Kit kit, KitUsage usage)
    {
        LinkedList<KitTier> applicableTiers = new LinkedList<>();
        KitTier maxTier = getMaxTier(kit, usage);
        LinkedList<KitTier> tiers = kit.getTiers();

        for(KitTier tier : tiers)
        {
            if ((tier.getUsesRequired() < maxTier.getUsesRequired() && tier.doesCascade())
                || tier.getUsesRequired() == maxTier.getUsesRequired())
            {
                applicableTiers.add(tier);
            }
        }
        return applicableTiers;
    }
}
