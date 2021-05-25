package com.goldensandsmc.tieredkits.bukkit.bukkitreflect;

import net.minecraft.server.v1_12_R1.NBTBase;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftMetaBlockStateHelper
{
    public CraftMetaBlockStateHelper()
    {

    }

    public static CraftMetaBlockState craftMetaBlockState(Material material, Map<String, NBTBase> unhandledTags,
                                                          String displayName, String locName, List<String> lore,
                                                          Map<Enchantment, Integer> enchantments, int repairCost,
                                                          int hideFlags, boolean unbreakable)
    {
        try
        {
            Class<?> craftMetaItemClass = ReflectionHelper.getCraftbukkitClass("inventory.CraftMetaItem");
            CraftMetaBlockState craftMetaBlockState = craftMetaBlockState(null, material);
            assert craftMetaBlockState != null;

            Field nbtField = ReflectionHelper.getField(craftMetaItemClass, "unhandledTags");
            nbtField.setAccessible(true);
            nbtField.set(craftMetaBlockState, new HashMap<>(unhandledTags));
            if(displayName != null)
            {
                craftMetaBlockState.setDisplayName(displayName);
            }
            if(locName != null)
            {
                craftMetaBlockState.setLocalizedName(locName);
            }
            if(lore != null && !lore.isEmpty())
            {
                craftMetaBlockState.setLore(lore);
            }
            else
            {
                craftMetaBlockState.setLore(new ArrayList<>());
            }
            Field enchantmentsField = ReflectionHelper.getField(craftMetaItemClass, "enchantments");
            enchantmentsField.setAccessible(true);
            if(enchantments != null && !enchantments.isEmpty())
            {
                enchantmentsField.set(craftMetaBlockState, new HashMap<>(enchantments));
            }
            else
            {
                enchantmentsField.set(craftMetaBlockState, new HashMap<Enchantment, Integer>());
            }
            craftMetaBlockState.setRepairCost(repairCost);
            Field hideFlagField = ReflectionHelper.getField(craftMetaItemClass, "hideFlag");
            hideFlagField.setAccessible(true);
            hideFlagField.setInt(craftMetaBlockState, hideFlags);
            craftMetaBlockState.setUnbreakable(unbreakable);
            return craftMetaBlockState;
        }
        catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static CraftMetaBlockState craftMetaBlockState(ItemMeta meta, Material material)
    {
        try
        {
            Class<?> craftMetaItemClass = ReflectionHelper.getCraftbukkitClass("inventory.CraftMetaItem");
            Class<?> craftMetaBlockStateClass = ReflectionHelper.getCraftbukkitClass("inventory.CraftMetaBlockState");
            Constructor<?> craftMetaBookConst = ReflectionHelper.getConstructor(craftMetaBlockStateClass, craftMetaItemClass, Material.class);
            craftMetaBookConst.setAccessible(true);
            return (CraftMetaBlockState) craftMetaBookConst.newInstance(craftMetaItemClass.cast(meta), material);
        }
        catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
