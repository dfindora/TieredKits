package com.goldensandsmc.tieredkits.bukkit.bukkitreflect;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBook;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CraftMetaBookHelper
{
    public CraftMetaBookHelper()
    {

    }

    public static CraftMetaBook craftMetaBook(String title, String author, List<String> pages)
    {
        try
        {
            Class<?> craftMetaItemClass = ReflectionHelper.getCraftbukkitClass("inventory.CraftMetaItem");
            Class<?> craftMetaBookClass = ReflectionHelper.getCraftbukkitClass("inventory.CraftMetaBook");
            Constructor<?> craftMetaBookConst = ReflectionHelper.getConstructor(craftMetaBookClass, craftMetaItemClass);
            craftMetaBookConst.setAccessible(true);
            CraftMetaBook craftMetaBook = (CraftMetaBook) craftMetaBookConst.newInstance((Object) null);
            craftMetaBook.setTitle(title);
            craftMetaBook.setAuthor(author);
            craftMetaBook.setPages(pages);
            return craftMetaBook;
        }
        catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException
                | IllegalAccessException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
