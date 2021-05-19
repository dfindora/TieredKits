package com.goldensandsmc.tieredkits.bukkit.adapters;

import com.goldensandsmc.tieredkits.bukkit.bukkitreflect.CraftMetaBookHelper;
import com.google.common.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBook;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CraftMetaBookTypeAdapter extends TypeAdapter<CraftMetaBook>
{
    @Override
    public void write(JsonWriter out, CraftMetaBook craftMetaBook) throws IOException
    {
        if(craftMetaBook == null)
        {
            out.nullValue();
        }
        else
        {
            String title = craftMetaBook.getTitle();
            String author = craftMetaBook.getAuthor();
            List<String> pages = craftMetaBook.getPages();
            Type pageType = (new TypeToken<List<String>>(){}).getType();
            out.beginObject();
            out.name("title").value(title);
            out.name("author").value(author);
            if(pages != null && !pages.isEmpty())
            {
                out.name("pages");
                ItemStackTypeAdapter.GSON.toJson(pages, pageType, out);
            }
            out.endObject();
        }
    }

    @Override
    public CraftMetaBook read(JsonReader in) throws IOException
    {
        if(in.peek() == JsonToken.NULL)
        {
            in.nextNull();
            return null;
        }
        else
        {
            String title = "";
            String author = "";
            List<String> pages = new ArrayList<>();
            Type pageType = (new TypeToken<List<String>>(){}).getType();

            in.beginObject();
            while (in.hasNext())
            {
                switch (in.nextName().toLowerCase())
                {
                    case "title":
                        title = in.nextString();
                        break;
                    case "author":
                        author = in.nextString();
                        break;
                    case "pages":
                        pages = ItemStackTypeAdapter.GSON.fromJson(in, pageType);
                        break;
                }
            }
            in.endObject();

            return CraftMetaBookHelper.craftMetaBook(title, author, pages);
        }
    }
}
