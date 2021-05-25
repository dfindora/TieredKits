package com.goldensandsmc.tieredkits.bukkit.adapters;

import com.goldensandsmc.tieredkits.bukkit.bukkitreflect.CraftMetaBlockStateHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack>
{

    public ItemStackTypeAdapter()
    {
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
                if (meta != null)
                {
                    try
                    {
                        out.name("meta");
                        Class<?> clazz = (meta instanceof EnchantmentStorageMeta) ? EnchantmentStorageMeta.class
                                                                                  : meta.getClass();
                        BaseTypeAdapter.GSON.toJson(meta, clazz, out);
                    }
                    catch (JsonIOException | AssertionError e)
                    {
                        e.printStackTrace();
                        System.out.println("Unable to serialize ItemMeta.");
                        System.out.println("Class = " + meta.getClass());
                        System.out.println("Item = " + value.getType().name());
                        out.nullValue();

                    }
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
                        if (in.peek() != JsonToken.NULL)
                        {
                            rawMeta = BaseTypeAdapter.GSON.fromJson(in, JsonElement.class);
                        }
                        break;
                }

            }

            in.endObject();
            if (type != null)
            {
                ItemStack item = new ItemStack(type, amount, durability);
                if(rawMeta != null && rawMeta.toString().contains("\"blockMaterial\""))
                {
                    item.setItemMeta(BaseTypeAdapter.deserializeMeta(rawMeta, CraftMetaBlockStateHelper.craftMetaBlockState(
                            Bukkit.getItemFactory().getItemMeta(type), type)));
                }
                else
                {
                    item.setItemMeta(BaseTypeAdapter.deserializeMeta(rawMeta, Bukkit.getItemFactory().getItemMeta(type)));
                }
                return item;
            }
            else
            {
                System.out.println("Unable to find Material for " + typeName);
                return null;
            }
        }
    }
}
