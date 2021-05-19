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

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBook;
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
                if (meta != null)
                {
                    System.out.println("class = " + meta.getClass());
                    System.out.println("name = " + meta.getDisplayName());
                    GSON.toJson(meta, meta.getClass(), out);
                }
                else
                {
                    System.out.println("meta was null.");
                }
            }

            out.endObject();
        }
    }

    @SuppressWarnings("deprecation")
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
            String typeName = "";
            int amount = 0;
            short durability = 0;
            JsonElement rawMeta = null;
            in.beginObject();
            while (in.hasNext())
            {
                switch (in.nextName())
                {
                    case "id":
                        int id = in.nextInt();
                        typeName = id + "";
                        for (Material material : Material.values())
                        {
                            if (material.getId() == id)
                            {
                                type = material;
                                break;
                            }
                        }
                    case "type":
                        try
                        {
                            typeName = in.nextString();
                            type = Material.valueOf(typeName);
                        }
                        catch (IllegalArgumentException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    case "data":
                    case "durability":
                    case "damage":
                        durability = (short) in.nextInt();
                        break;
                    case "count":
                    case "amount":
                        amount = in.nextInt();
                        break;
                    case "meta":
                    case "metadata":
                        rawMeta = GSON.fromJson(in, JsonElement.class);
                        break;
                }

            }

            in.endObject();
            if (type != null)
            {
                ItemStack item = new ItemStack(type, amount, durability);
                item.setItemMeta(deserializeMeta(rawMeta, type));
                return item;
            }
            else
            {
                System.out.println("Unable to find Material for " + typeName);
                return null;
            }
        }
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

            for(Entry<String, JsonElement> entry : ((JsonObject) element).entrySet())
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
