package com.goldensandsmc.tieredkits.bukkit.adapters;

import com.goldensandsmc.tieredkits.bukkit.EnchantmentHelper;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentTypeAdapter extends TypeAdapter<Enchantment>
{
    public EnchantmentTypeAdapter()
    {
    }

    public void write(JsonWriter out, Enchantment enchantment) throws IOException
    {
        if (enchantment == null)
        {
            out.nullValue();
        }
        else
        {
            out.value(enchantment.getName());
        }
    }

    public Enchantment read(JsonReader in) throws IOException
    {
        if (in.peek() == JsonToken.NULL)
        {
            in.nextNull();
            return null;
        }
        else
        {
            return EnchantmentHelper.getEnchantment(in.nextString());
        }
    }
}
