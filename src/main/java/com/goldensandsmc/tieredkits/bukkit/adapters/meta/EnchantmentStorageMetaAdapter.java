package com.goldensandsmc.tieredkits.bukkit.adapters.meta;

import com.goldensandsmc.tieredkits.bukkit.EnchantmentHelper;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.io.IOException;
import java.util.Map;

public class EnchantmentStorageMetaAdapter extends TypeAdapter<EnchantmentStorageMeta>
{
    @Override
    public void write(JsonWriter jsonWriter, EnchantmentStorageMeta enchantmentStorageMeta) throws IOException
    {
        System.out.println("parsing json.");
        jsonWriter.beginObject();
        jsonWriter.name("storedEnchantments");
        jsonWriter.beginObject();
        for (Map.Entry<Enchantment, Integer> enchantment : enchantmentStorageMeta.getStoredEnchants().entrySet())
        {
            jsonWriter.name(enchantment.getKey().getName()).value(enchantment.getValue());
        }
        jsonWriter.endObject();

        jsonWriter.endObject();
    }

    @Override
    public EnchantmentStorageMeta read(JsonReader jsonReader) throws IOException
    {
        EnchantmentStorageMeta meta =
                (EnchantmentStorageMeta) Bukkit.getItemFactory().getItemMeta(Material.ENCHANTED_BOOK);
        jsonReader.beginObject();
        if(jsonReader.nextName().equalsIgnoreCase("storedenchantments"))
        {
            jsonReader.beginObject();
            while (jsonReader.hasNext() && jsonReader.peek() != JsonToken.END_OBJECT)
            {
                String enchantmentName = jsonReader.nextName();
                meta.addStoredEnchant(EnchantmentHelper.getEnchantment(enchantmentName), jsonReader.nextInt(), true);
            }
            jsonReader.endObject();
        }

        jsonReader.endObject();
        return meta;
    }
}
