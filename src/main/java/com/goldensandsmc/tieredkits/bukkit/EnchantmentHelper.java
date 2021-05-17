package com.goldensandsmc.tieredkits.bukkit;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentHelper
{
    public EnchantmentHelper()
    {
    }

    public static Enchantment[] getAllEnchantments()
    {
        return Enchantment.values();
    }

    public static Enchantment getEnchantment(String name)
    {
        for (Enchantment enchantment : getAllEnchantments())
        {
            if (enchantment.getName().equalsIgnoreCase(name))
            {
                return enchantment;
            }
        }
        return null;
    }
}
