package com.goldensandsmc.tieredkits.bukkit;

import com.goldensandsmc.tieredkits.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class GoodnessGracious
{
    public GoodnessGracious()
    {
    }

    public static JsonObject toJson(ConfigurationSerializable serializable)
    {
        Map<String, Object> serialized = serializable.serialize();

        for (Entry<String, Object> entry : serialized.entrySet())
        {
            if (entry.getValue() != null && entry.getValue() instanceof ConfigurationSerializable)
            {
                JsonObject element = toJson((ConfigurationSerializable) entry.getValue());
                element.addProperty("!!type", entry.getValue().getClass().getCanonicalName());
                entry.setValue(element);
            }
        }

        return JsonUtils.getGson().toJsonTree(serialized).getAsJsonObject();
    }

    public static <T extends ConfigurationSerializable> T fromJson(JsonElement serialized, Class<T> clazz) throws
            ReflectiveOperationException
    {
        Type type = (new TypeToken<HashMap<String, Object>>()
        {
        }).getType();
        HashMap<String, Object> map = JsonUtils.fromJson(serialized, type);
        return fromJson(map, clazz);
    }

    public static <T extends ConfigurationSerializable> T fromJson(Map<String, Object> serialized, Class<T> clazz) throws
            ReflectiveOperationException
    {
        for(Entry<String, Object> entry : serialized.entrySet())
        {
            if (entry.getValue() != null && entry.getValue() instanceof Map)
            {
                Map<String, Object> element = (Map) entry.getValue();
                if (element.containsKey("!!type"))
                {
                    Class<? extends ConfigurationSerializable> found_class =
                            (Class<? extends ConfigurationSerializable>) Class.forName((String) element.get("!!type"));
                    entry.setValue(fromJson(element, found_class));
                }
            }
        }

        Method method_deserialize;
        try
        {
            method_deserialize = clazz.getDeclaredMethod("valueOf", Map.class);
            method_deserialize.setAccessible(true);
            return (T) method_deserialize.invoke((Object) null, serialized);
        }
        catch (NoSuchMethodException noSuchMethodException)
        {
            try
            {
                method_deserialize = clazz.getDeclaredMethod("deserialize", Map.class);
                method_deserialize.setAccessible(true);
                return (T) method_deserialize.invoke((Object) null, serialized);
            }
            catch (NoSuchMethodException noSuchMethodException1)
            {
                try
                {
                    Constructor<T> ctor = clazz.getDeclaredConstructor(Map.class);
                    ctor.setAccessible(true);
                    return (T) ctor.newInstance(serialized);
                }
                catch (NoSuchMethodException noSuchMethodException2)
                {
                    noSuchMethodException2.printStackTrace();
                    return null;
                }
            }
        }
    }
}
