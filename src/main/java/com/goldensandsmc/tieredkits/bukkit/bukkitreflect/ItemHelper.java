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
        Class<?> class_craftitemstack = ReflectionHelper.getCraftbukkitClass("inventory.CraftItemStack");
        Class<?> class_mojangsonparser = ReflectionHelper.getNMSClass("MojangsonParser");
        Class<?> class_itemstack = ReflectionHelper.getNMSClass("ItemStack");
        Class<?> class_nbttagcompound = ReflectionHelper.getNMSClass("NBTTagCompound");
        Method method_parse = class_mojangsonparser.getDeclaredMethod("parse", String.class);
        Method method_asnmscopy = class_craftitemstack.getDeclaredMethod("asNMSCopy", ItemStack.class);
        Method method_asbukkitcopy = class_craftitemstack.getDeclaredMethod("asBukkitCopy", class_itemstack);
        Method method_settag = class_itemstack.getDeclaredMethod("setTag", class_nbttagcompound);
        Object nbt_tag = method_parse.invoke(null, meta);
        Object nms_copy = method_asnmscopy.invoke(null, item);
        method_settag.invoke(nms_copy, nbt_tag);
        ItemStack new_item = (ItemStack) method_asbukkitcopy.invoke(null, nms_copy);
        return new_item.getItemMeta();
    }
}
