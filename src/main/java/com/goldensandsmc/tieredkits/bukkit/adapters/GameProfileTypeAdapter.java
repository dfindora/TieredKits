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
                Field field_id = ReflectionHelper.getField(value.getClass(), "id");
                field_id.setAccessible(true);
                Field field_name = ReflectionHelper.getField(value.getClass(), "name");
                field_name.setAccessible(true);
                id = (UUID) field_id.get(value);
                name = (String) field_name.get(value);
            }
            catch (ReflectiveOperationException var7)
            {
                var7.printStackTrace();
            }

            out.beginObject();
            if (id != null)
            {
                out.name("id");
                ItemStackTypeAdapter.GSON.toJson(id, UUID.class, out);
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
                String var4 = in.nextName().toLowerCase();
                byte var5 = -1;
                switch (var4.hashCode())
                {
                    case -265713450:
                        if (var4.equals("username"))
                        {
                            var5 = 3;
                        }
                        break;
                    case 3355:
                        if (var4.equals("id"))
                        {
                            var5 = 0;
                        }
                        break;
                    case 3373707:
                        if (var4.equals("name"))
                        {
                            var5 = 2;
                        }
                        break;
                    case 3601339:
                        if (var4.equals("uuid"))
                        {
                            var5 = 1;
                        }
                }

                switch (var5)
                {
                    case 0:
                    case 1:
                        id = ItemStackTypeAdapter.GSON.fromJson(in, UUID.class);
                        break;
                    case 2:
                    case 3:
                        name = in.nextString();
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
                    Constructor<?> constructor_gameprofile = ReflectionHelper
                            .getConstructor(getGameProfileClass(), UUID.class, String.class);
                    return constructor_gameprofile.newInstance(id, name);
                }
                catch (ReflectiveOperationException var6)
                {
                    var6.printStackTrace();
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
        catch (ClassNotFoundException var1)
        {
            return Class.forName("com.mojang.authlib.GameProfile");
        }
    }
}
