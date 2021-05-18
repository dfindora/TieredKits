package com.goldensandsmc.tieredkits.bukkit.adapters;

import com.goldensandsmc.tieredkits.bukkit.bukkitreflect.ReflectionHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack>
{
    protected static final Gson GSON = gsonBuilderWithItemSerializers().create();

    public ItemStackTypeAdapter()
    {
    }

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

        builder.registerTypeAdapter(Color.class, new ColorTypeAdapter());
        builder.registerTypeAdapter(Enchantment.class, new EnchantmentTypeAdapter());
        builder.registerTypeAdapter(FireworkEffect.class, new FireworkEffectTypeAdapter());
        builder.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());
        return builder;
    }

    public void write(JsonWriter out, ItemStack value) throws IOException
    {
        if (value == null)
        {
            out.nullValue();
        }
        else
        {
            out.beginObject();
            out.name("type").value(value.getType().name());
            out.name("amount").value(value.getAmount());
            out.name("durability").value(value.getDurability());
            if (value.hasItemMeta())
            {
                ItemMeta meta = value.getItemMeta();
                out.name("meta");
                GSON.toJson(meta, meta.getClass(), out);
            }

            out.endObject();
        }
    }

    public ItemStack read(JsonReader in) throws IOException
    {
        if (in.peek() == JsonToken.NULL)
        {
            in.nextNull();
            return null;
        }
        else
        {
            Material type = null;
            int amount = 0;
            short durability = 0;
            JsonElement raw_meta = null;
            in.beginObject();

            while (true)
            {
                label74:
                while (in.hasNext())
                {
                    String var6 = in.nextName();
                    byte var7 = -1;
                    switch (var6.hashCode())
                    {
                        case -1413853096:
                            if (var6.equals("amount"))
                            {
                                var7 = 6;
                            }
                            break;
                        case -1339126929:
                            if (var6.equals("damage"))
                            {
                                var7 = 3;
                            }
                            break;
                        case -450004177:
                            if (var6.equals("metadata"))
                            {
                                var7 = 8;
                            }
                            break;
                        case 3355:
                            if (var6.equals("id"))
                            {
                                var7 = 0;
                            }
                            break;
                        case 3076010:
                            if (var6.equals("data"))
                            {
                                var7 = 2;
                            }
                            break;
                        case 3347973:
                            if (var6.equals("meta"))
                            {
                                var7 = 7;
                            }
                            break;
                        case 3575610:
                            if (var6.equals("type"))
                            {
                                var7 = 1;
                            }
                            break;
                        case 94851343:
                            if (var6.equals("count"))
                            {
                                var7 = 5;
                            }
                            break;
                        case 716086281:
                            if (var6.equals("durability"))
                            {
                                var7 = 4;
                            }
                    }

                    switch (var7)
                    {
                        case 0:
                            int id = in.nextInt();
                            Material[] var9 = Material.values();
                            int var10 = var9.length;
                            int var11 = 0;

                            while (true)
                            {
                                if (var11 >= var10)
                                {
                                    continue label74;
                                }

                                Material material = var9[var11];
                                if (material.getId() == id)
                                {
                                    type = material;
                                    continue label74;
                                }

                                ++var11;
                            }
                        case 1:
                            try
                            {
                                type = Material.valueOf(in.nextString());
                            }
                            catch (IllegalArgumentException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                        case 2:
                        case 3:
                        case 4:
                            durability = (short) in.nextInt();
                            break;
                        case 5:
                        case 6:
                            amount = in.nextInt();
                            break;
                        case 7:
                        case 8:
                            raw_meta = GSON.fromJson(in, JsonElement.class);
                    }
                }

                in.endObject();
                ItemStack item = new ItemStack(type, amount, durability);
                item.setItemMeta(deserializeMeta(raw_meta, type));
                return item;
            }
        }
    }

    public static <T extends ItemMeta> T deserializeMeta(JsonElement element, Material type)
    {
        if (element instanceof JsonObject)
        {
            if (type == null)
            {
                type = Material.STONE;
            }

            ItemMeta dummy_meta = Bukkit.getItemFactory().getItemMeta(type);

            for(Entry<String, JsonElement> entry : ((JsonObject) element).entrySet())
            {
                try
                {
                    Field field = ReflectionHelper.getField(dummy_meta.getClass(), entry.getKey());
                    field.setAccessible(true);
                    field.set(dummy_meta, GSON.fromJson(entry.getValue(), field.getGenericType()));
                }
                catch (ReflectiveOperationException noSuchFieldException)
                {
                    noSuchFieldException.printStackTrace();
                }
            }

            return (T)dummy_meta;
        }
        else
        {
            return null;
        }
    }

    public static JsonElement serializeMeta(ItemMeta meta)
    {
        return meta == null ? JsonNull.INSTANCE : GSON.toJsonTree(meta, meta.getClass());
    }
}
