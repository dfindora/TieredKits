package com.goldensandsmc.tieredkits.bukkit.adapters;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
                out.name("meta");
                if (meta != null)
                {
                    System.out.println("class = " + meta.getClass());
                    System.out.println("name = " + meta.getDisplayName());
                    BaseTypeAdapter.GSON.toJson(meta, meta.getClass(), out);
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
                        rawMeta = BaseTypeAdapter.GSON.fromJson(in, JsonElement.class);
                        break;
                }

            }

            in.endObject();
            if (type != null)
            {
                ItemStack item = new ItemStack(type, amount, durability);
                item.setItemMeta(BaseTypeAdapter.deserializeMeta(rawMeta, type));
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
