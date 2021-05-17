package com.goldensandsmc.tieredkits.bukkit.adapters;

import com.goldensandsmc.tieredkits.bukkit.bukkitreflect.ChatComponentHelper;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class IChatBaseComponentTypeAdapter extends TypeAdapter
{
    public IChatBaseComponentTypeAdapter()
    {
    }

    public void write(JsonWriter out, Object value) throws IOException
    {
        out.value(ChatComponentHelper.stringFromChatComponent(value));
    }

    public Object read(JsonReader in) throws IOException
    {
        return ChatComponentHelper.chatComponentFromString(in.nextString());
    }
}
