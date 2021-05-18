package com.goldensandsmc.tieredkits.bukkit.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;

public class FireworkEffectTypeAdapter extends TypeAdapter<FireworkEffect>
{
    public FireworkEffectTypeAdapter()
    {
    }

    public void write(JsonWriter out, FireworkEffect value) throws IOException
    {
        if (value == null)
        {
            out.nullValue();
        }
        else
        {
            Type color_list_type = (new TypeToken<List<Color>>(){}).getType();
            out.beginObject();
            out.name("type").value(value.getType().name());
            if (value.getColors() != null && !value.getColors().isEmpty())
            {
                out.name("colors");
                ItemStackTypeAdapter.GSON.toJson(value.getColors(), color_list_type, out);
            }

            if (value.getFadeColors() != null && !value.getFadeColors().isEmpty())
            {
                out.name("fadeColors");
                ItemStackTypeAdapter.GSON.toJson(value.getColors(), color_list_type, out);
            }

            if (value.hasFlicker())
            {
                out.name("flicker").value(true);
            }

            if (value.hasTrail())
            {
                out.name("trail").value(true);
            }

            out.endObject();
        }
    }

    public FireworkEffect read(JsonReader in) throws IOException
    {
        if (in.peek() == JsonToken.NULL)
        {
            in.nextNull();
            return null;
        }
        else
        {
            Type color_list_type = (new TypeToken<LinkedList<Color>>(){}).getType();
            org.bukkit.FireworkEffect.Type fireworkType = org.bukkit.FireworkEffect.Type.BALL;
            List<Color> colors = new LinkedList<>();
            List<Color> fadeColors = new LinkedList<>();
            boolean flicker = false;
            boolean trail = false;
            in.beginObject();

            while (in.hasNext())
            {
                switch (in.nextName().toLowerCase())
                {
                    case "type":
                    case "shape":
                        fireworkType = org.bukkit.FireworkEffect.Type.valueOf(in.nextString().toUpperCase());
                        break;
                    case "colors":
                        colors = ItemStackTypeAdapter.GSON.fromJson(in, color_list_type);
                        break;
                    case "fade-colors":
                    case "fade_colors":
                    case "fadecolors":
                        fadeColors = ItemStackTypeAdapter.GSON.fromJson(in, color_list_type);
                        break;
                    case "flicker":
                        flicker = in.nextBoolean();
                        break;
                    case "trail":
                        trail = in.nextBoolean();
                        break;
                }
            }
            in.endObject();
            return FireworkEffect.builder().with(fireworkType).withColor(colors).withFade(fadeColors)
                                 .flicker(flicker).trail(trail).build();
        }
    }
}
