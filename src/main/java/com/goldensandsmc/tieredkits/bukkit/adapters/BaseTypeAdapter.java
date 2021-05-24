package com.goldensandsmc.tieredkits.bukkit.adapters;

import com.goldensandsmc.tieredkits.bukkit.bukkitreflect.ReflectionHelper;
import com.google.gson.*;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBook;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.Map;

public class BaseTypeAdapter
{
    protected static final Gson GSON = gsonBuilderWithItemSerializers().create();

    public static GsonBuilder gsonBuilderWithItemSerializers()
    {
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();

        try
        {
            builder.registerTypeAdapter(Class.forName("net.minecraft.server.v1_12_R1.Enchantment"),
                                        new EnchantmentTypeAdapter());
        }
        catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
        }

        try
        {
            builder.registerTypeAdapter(ReflectionHelper.getCraftbukkitClass("inventory.CraftItemStack"),
                                        new ItemStackTypeAdapter());
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try
        {
            builder.registerTypeAdapter(ReflectionHelper.getCraftbukkitClass("inventory.CraftMetaBookSigned"),
                                        new CraftMetaBookTypeAdapter());
        }
        catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
        }

        builder.registerTypeAdapter(Color.class, new ColorTypeAdapter());
        builder.registerTypeAdapter(Enchantment.class, new EnchantmentTypeAdapter());
        builder.registerTypeAdapter(FireworkEffect.class, new FireworkEffectTypeAdapter());
        builder.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());
        builder.registerTypeAdapter(CraftMetaBook.class, new CraftMetaBookTypeAdapter());
        builder.registerTypeAdapter(IChatBaseComponent.class, new IChatBaseComponentTypeAdapter());
        return builder;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ItemMeta> T deserializeMeta(JsonElement element, Material type)
    {
        if (element instanceof JsonObject)
        {
            if (type == null)
            {
                type = Material.STONE;
            }

            ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(type);

            for(Map.Entry<String, JsonElement> entry : ((JsonObject) element).entrySet())
            {
                try
                {
                    Field field = ReflectionHelper.getField(itemMeta.getClass(), entry.getKey());
                    field.setAccessible(true);
                    field.set(itemMeta, GSON.fromJson(entry.getValue(), field.getGenericType()));
                }
                catch (ReflectiveOperationException noSuchFieldException)
                {
                    noSuchFieldException.printStackTrace();
                }
            }

            return (T)itemMeta;
        }
        else
        {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static JsonElement serializeMeta(ItemMeta meta)
    {
        return meta == null ? JsonNull.INSTANCE : GSON.toJsonTree(meta, meta.getClass());
    }
}
