package com.goldensandsmc.tieredkits.bukkit.adapters;

import com.goldensandsmc.tieredkits.bukkit.Utils;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.bukkit.Color;

public class ColorTypeAdapter extends TypeAdapter<Color>
{
    public ColorTypeAdapter()
    {
    }

    public void write(JsonWriter out, Color color) throws IOException
    {
        if (color == null)
        {
            out.nullValue();
        }
        else
        {
            String name = Utils.getNameFromColor(color);
            if (name != null)
            {
                out.value(name.toUpperCase());
            }
            else
            {
                out.beginObject();
                out.name("r").value(color.getRed());
                out.name("g").value(color.getGreen());
                out.name("b").value(color.getBlue());
                out.endObject();
            }
        }
    }

    public Color read(JsonReader in) throws IOException
    {
        int r = 0;
        int g = 0;
        int b = 0;
        switch (in.peek())
        {
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                return Color.fromRGB(in.nextInt());
            case STRING:
                String name = in.nextString();
                Color color = Utils.getColorFromName(name);
                if (color == null)
                {
                    throw new NoSuchElementException(name);
                }

                return color;
            case BEGIN_ARRAY:
                in.beginArray();
                r = in.nextInt();
                g = in.nextInt();
                b = in.nextInt();
                in.endArray();
                return Color.fromRGB(r, g, b);
            case BEGIN_OBJECT:
                in.beginObject();

                while (in.hasNext())
                {
                    switch (in.nextName().toLowerCase())
                    {
                        case "r":
                        case "red":
                            r = in.nextInt();
                            break;
                        case "g":
                        case "green":
                            g = in.nextInt();
                            break;
                        case "b":
                        case "blue":
                            b = in.nextInt();
                            break;
                    }
                }

                in.endObject();
                return Color.fromRGB(r, g, b);
            default:
                throw new JsonParseException(
                        "Expected [NULL, NUMBER, STRING, BEGIN_ARRAY, BEGIN_OBJECT] but was " + in.peek() + " at " + in
                                .getPath());
        }
    }
}