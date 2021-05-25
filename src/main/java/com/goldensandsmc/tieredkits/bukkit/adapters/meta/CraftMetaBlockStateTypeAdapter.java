package com.goldensandsmc.tieredkits.bukkit.adapters.meta;

import com.goldensandsmc.tieredkits.bukkit.adapters.BaseTypeAdapter;
import com.goldensandsmc.tieredkits.bukkit.bukkitreflect.CraftMetaBlockStateHelper;
import com.goldensandsmc.tieredkits.bukkit.bukkitreflect.ReflectionHelper;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.server.v1_12_R1.NBTBase;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBlockState;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftMetaBlockStateTypeAdapter extends TypeAdapter<CraftMetaBlockState>
{
    @Override
    @SuppressWarnings("unchecked")
    public void write(JsonWriter jsonWriter, CraftMetaBlockState craftMetaBlockState) throws IOException
    {
        try
        {
            jsonWriter.beginObject();
            String material = (String) craftMetaBlockState.serialize().get("blockMaterial");
            jsonWriter.name("blockMaterial").value(material);

            Class<?> craftMetaItemClass = ReflectionHelper.getCraftbukkitClass("inventory.CraftMetaItem");
            Field nbtField = ReflectionHelper.getField(craftMetaItemClass, "unhandledTags");
            nbtField.setAccessible(true);
            Map<String, NBTBase> unhandledTags = (Map<String, NBTBase>) nbtField
                    .get(craftMetaItemClass.cast(craftMetaBlockState));

            if (!unhandledTags.isEmpty())
            {
                jsonWriter.name("unhandledTags");
                jsonWriter.beginObject();
                for(Map.Entry<String, NBTBase> unhandledTag : unhandledTags.entrySet())
                {
                    jsonWriter.name(unhandledTag.getKey());
                    BaseTypeAdapter.GSON.toJson(unhandledTag.getValue(), NBTBase.class, jsonWriter);
                }
                jsonWriter.endObject();
            }

            if (craftMetaBlockState.hasDisplayName())
            {
                jsonWriter.name("displayName").value(craftMetaBlockState.getDisplayName());
            }
            if (craftMetaBlockState.hasLocalizedName())
            {
                jsonWriter.name("locName").value(craftMetaBlockState.getLocalizedName());
            }

            if (craftMetaBlockState.hasLore())
            {
                jsonWriter.name("lore");
                jsonWriter.beginArray();
                for (String loreStr : craftMetaBlockState.getLore())
                {
                    jsonWriter.value(loreStr);
                }
                jsonWriter.endArray();
            }

            Map<Enchantment, Integer> enchantments = craftMetaBlockState.getEnchants();
            if(!enchantments.isEmpty())
            {
                jsonWriter.name("enchantments");
                jsonWriter.beginObject();
                for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet())
                {
                    jsonWriter.name(enchantment.getKey().getName()).value(enchantment.getValue());
                }
                jsonWriter.endObject();
            }

            jsonWriter.name("repairCost").value(craftMetaBlockState.getRepairCost());
            Field hideFlagField = ReflectionHelper.getField(craftMetaItemClass, "hideFlag");
            hideFlagField.setAccessible(true);
            jsonWriter.name("hideFlag").value((int)hideFlagField.get(craftMetaItemClass.cast(craftMetaBlockState)));
            jsonWriter.name("unbreakable").value(craftMetaBlockState.isUnbreakable());
            jsonWriter.endObject();
        }
        catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public CraftMetaBlockState read(JsonReader jsonReader) throws IOException
    {
        jsonReader.beginObject();
        Material material = Material.AIR;
        Map<String, NBTBase> unhandledTags = new HashMap<>();
        String displayName = null;
        String locName = null;
        List<String> lore = new ArrayList<>();
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        int repairCost = 0;
        int hideFlag = 0;
        boolean unbreakable = false;
        while(jsonReader.hasNext())
        {
            String token = jsonReader.nextName().toLowerCase();
            switch (token)
            {
                case "blockmaterial":
                    material = Material.getMaterial(jsonReader.nextString());
                    break;
                case "unhandledtags":
                    jsonReader.beginObject();
                    while (jsonReader.peek() != JsonToken.END_OBJECT)
                    {
                        String name = jsonReader.nextName();
                        unhandledTags.put(name, BaseTypeAdapter.GSON.fromJson(jsonReader, NBTBase.class));
                    }
                    jsonReader.endObject();
                    break;
                case "displayname":
                    displayName = jsonReader.nextString();
                    break;
                case "locname":
                    locName = jsonReader.nextString();
                    break;
                case "lore":
                    jsonReader.beginArray();
                    while(jsonReader.hasNext() && jsonReader.peek() != JsonToken.END_ARRAY)
                    {
                        lore.add(jsonReader.nextString());
                    }
                    jsonReader.endArray();
                    break;
                case "enchantments":
                    jsonReader.beginObject();
                    while(jsonReader.hasNext() && jsonReader.peek() != JsonToken.END_OBJECT)
                    {
                        enchantments.put(Enchantment.getByName(jsonReader.nextName()), jsonReader.nextInt());
                    }
                    jsonReader.endObject();
                    break;
                case "repaircost":
                    repairCost = jsonReader.nextInt();
                    break;
                case "hideflag":
                    hideFlag = jsonReader.nextInt();
                    break;
                case "unbreakable":
                    unbreakable = jsonReader.nextBoolean();
                    break;
            }
        }
        jsonReader.endObject();
        return CraftMetaBlockStateHelper.craftMetaBlockState(material, unhandledTags, displayName, locName, lore,
                                                             enchantments, repairCost, hideFlag, unbreakable);
    }
}
