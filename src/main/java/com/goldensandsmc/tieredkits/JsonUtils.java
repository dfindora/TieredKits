package com.goldensandsmc.tieredkits;

import com.goldensandsmc.tieredkits.bukkit.adapters.BaseTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class JsonUtils
{
    private static final Gson gson =
            BaseTypeAdapter.gsonBuilderWithItemSerializers().serializeNulls().setPrettyPrinting()
                           .disableHtmlEscaping().create();

    public JsonUtils()
    {
    }

    public static <T> JsonElement toJson(T obj)
    {
        return gson.toJsonTree(obj, obj.getClass());
    }

    public static <T> String toJsonString(T obj)
    {
        return gson.toJson(toJson(obj));
    }

    public static <T> T fromJson(String json, TypeToken<T> type) throws JsonSyntaxException, ExceptionInInitializerError
    {
        return gson.fromJson(json, type.getType());
    }

    public static <T> T fromJson(String json, Type type) throws JsonSyntaxException, ExceptionInInitializerError
    {
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Class<T> type) throws JsonSyntaxException, ExceptionInInitializerError
    {
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(JsonElement json, TypeToken<T> type) throws JsonSyntaxException,
            ExceptionInInitializerError
    {
        return gson.fromJson(json, type.getType());
    }

    public static <T> T fromJson(JsonElement json, Type type) throws JsonSyntaxException, ExceptionInInitializerError
    {
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(JsonElement json, Class<T> type) throws JsonSyntaxException,
            ExceptionInInitializerError
    {
        return gson.fromJson(json, type);
    }

    public static Gson getGson()
    {
        return gson;
    }
}
