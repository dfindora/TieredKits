package com.goldensandsmc.tieredkits.bukkit.adapters;

import com.goldensandsmc.tieredkits.bukkit.bukkitreflect.ReflectionHelper;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class GameProfileTypeAdapter extends TypeAdapter
{
    public GameProfileTypeAdapter()
    {
    }

    public void write(JsonWriter out, Object value) throws IOException
    {
        if (value == null)
        {
            out.nullValue();
        }
        else
        {
            UUID id = null;
            String name = null;

            try
            {
                Field fieldId = ReflectionHelper.getField(value.getClass(), "id");
                fieldId.setAccessible(true);
                Field fieldName = ReflectionHelper.getField(value.getClass(), "name");
                fieldName.setAccessible(true);
                id = (UUID) fieldId.get(value);
                name = (String) fieldName.get(value);
            }
            catch (ReflectiveOperationException e)
            {
                e.printStackTrace();
            }

            out.beginObject();
            if (id != null)
            {
                out.name("id");
                BaseTypeAdapter.GSON.toJson(id, UUID.class, out);
            }

            if (name != null)
            {
                out.name("name").value(name);
            }

            out.endObject();
        }
    }

    public Object read(JsonReader in) throws IOException
    {
        if (in.peek() == JsonToken.NULL)
        {
            in.nextNull();
            return null;
        }
        else
        {
            UUID id = null;
            String name = null;
            in.beginObject();

            while (in.hasNext())
            {
                switch (in.nextName())
                {
                    case "id":
                    case "uuid":
                        id = BaseTypeAdapter.GSON.fromJson(in, UUID.class);
                        break;
                    case "name":
                    case "username":
                        name = in.nextString();
                        break;
                }
            }

            in.endObject();
            if (id == null && name == null)
            {
                return null;
            }
            else
            {
                OfflinePlayer player;
                if (id == null)
                {
                    player = Bukkit.getServer().getOfflinePlayer(name);
                    if (player.hasPlayedBefore())
                    {
                        id = player.getUniqueId();
                    }
                }

                if (name == null)
                {
                    player = Bukkit.getServer().getOfflinePlayer(id);
                    if (player.hasPlayedBefore())
                    {
                        name = player.getName();
                    }
                }

                try
                {
                    Constructor<?> constructorGameProfile = ReflectionHelper
                            .getConstructor(getGameProfileClass(), UUID.class, String.class);
                    return constructorGameProfile.newInstance(id, name);
                }
                catch (ReflectiveOperationException e)
                {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public static Class<?> getGameProfileClass() throws ClassNotFoundException
    {
        try
        {
            return Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
        }
        catch (ClassNotFoundException e)
        {
            return Class.forName("com.mojang.authlib.GameProfile");
        }
    }
}
