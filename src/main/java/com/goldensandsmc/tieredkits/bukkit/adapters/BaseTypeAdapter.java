package com.goldensandsmc.tieredkits.bukkit.adapters;

import com.goldensandsmc.tieredkits.bukkit.adapters.meta.*;
import com.goldensandsmc.tieredkits.bukkit.bukkitreflect.ReflectionHelper;
import com.google.gson.*;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.NBTBase;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBlockState;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBook;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.Map;

public class BaseTypeAdapter
{
    public static final Gson GSON = gsonBuilderWithItemSerializers().create();

    public static GsonBuilder gsonBuilderWithItemSerializers()
    {
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();

        try
        {
            builder.registerTypeAdapter(Class.forName("net.minecraft.server.v1_12_R1.Enchantment"),
                                        new EnchantmentTypeAdapter());
            builder.registerTypeAdapter(ReflectionHelper.getCraftbukkitClass("inventory.CraftItemStack"),
                                        new ItemStackTypeAdapter());
            builder.registerTypeAdapter(ReflectionHelper.getCraftbukkitClass("inventory.CraftMetaBookSigned"),
                                        new CraftMetaBookTypeAdapter());
            builder.registerTypeAdapter(ReflectionHelper.getCraftbukkitClass("inventory.CraftMetaSpawnEgg"),
                                        new CraftMetaSpawnEggTypeAdapter());
            builder.registerTypeAdapter(ReflectionHelper.getCraftbukkitClass("inventory.CraftMetaBlockState"),
                                        new CraftMetaBlockStateTypeAdapter());
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
        builder.registerTypeAdapter(EnchantmentStorageMeta.class, new EnchantmentStorageMetaAdapter());
        builder.registerTypeAdapter(NBTBase.class, new NBTBaseTypeAdapter());
        return builder;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ItemMeta> T deserializeMeta(JsonElement element, ItemMeta meta)
    {
        if (element instanceof JsonObject)
        {
            ItemMeta itemMeta = (meta == null) ? Bukkit.getItemFactory().getItemMeta(Material.STONE) : meta;

            try
            {
                if(itemMeta.getClass() == CraftMetaBlockState.class)
                {
                    itemMeta = GSON.fromJson(element, CraftMetaBlockState.class);
                }
                else
                {
                    for(Map.Entry<String, JsonElement> entry : ((JsonObject) element).entrySet())
                    {
                        Field field = ReflectionHelper.getField(itemMeta.getClass(), entry.getKey());
                        field.setAccessible(true);
                        field.set(itemMeta, GSON.fromJson(entry.getValue(), field.getGenericType()));
                    }
                }
            }
            catch (ReflectiveOperationException e)
            {
                e.printStackTrace();
            }
            catch (JsonSyntaxException e)
            {
                e.printStackTrace();
                System.err.println(element);
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
