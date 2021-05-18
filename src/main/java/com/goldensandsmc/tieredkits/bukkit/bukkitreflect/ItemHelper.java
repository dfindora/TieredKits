package com.goldensandsmc.tieredkits.bukkit.bukkitreflect;

import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemHelper
{
    public ItemHelper()
    {
    }

    public static ItemMeta parseMeta(ItemStack item, String meta) throws Exception
    {
        Class<?> classCraftItemStack = ReflectionHelper.getCraftbukkitClass("inventory.CraftItemStack");
        Class<?> classMojangsonParser = ReflectionHelper.getNMSClass("MojangsonParser");
        Class<?> classItemStack = ReflectionHelper.getNMSClass("ItemStack");
        Class<?> classNBTTagCompound = ReflectionHelper.getNMSClass("NBTTagCompound");
        Method methodParse = classMojangsonParser.getDeclaredMethod("parse", String.class);
        Method methodAsNMSCopy = classCraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
        Method methodAsBukkitCopy = classCraftItemStack.getDeclaredMethod("asBukkitCopy", classItemStack);
        Method methodSetTag = classItemStack.getDeclaredMethod("setTag", classNBTTagCompound);
        Object nbtTag = methodParse.invoke(null, meta);
        Object nmsCopy = methodAsNMSCopy.invoke(null, item);
        methodSetTag.invoke(nmsCopy, nbtTag);
        ItemStack newItem = (ItemStack) methodAsBukkitCopy.invoke(null, nmsCopy);
        return newItem.getItemMeta();
    }
}
