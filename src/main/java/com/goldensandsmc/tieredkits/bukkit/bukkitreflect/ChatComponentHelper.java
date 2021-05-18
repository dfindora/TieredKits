package com.goldensandsmc.tieredkits.bukkit.bukkitreflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ChatComponentHelper
{
    public ChatComponentHelper()
    {
    }

    public static Object chatComponentFromString(String str)
    {
        try
        {
            Class<?> classIChatBaseComponent$ChatSerializer =
                    ReflectionHelper.getNMSClass("IChatBaseComponent$ChatSerializer");
            Method method_a = ReflectionHelper.getMethod(classIChatBaseComponent$ChatSerializer, "a", String.class);
            method_a.setAccessible(true);

            try
            {
                return method_a.invoke(null, str);
            }
            catch (Exception e)
            {
                Class<?> classChatMessage = ReflectionHelper.getNMSClass("ChatMessage");
                Constructor<?> constructorChatMessage =
                        ReflectionHelper.getConstructor(classChatMessage, String.class,
                                                        Object[].class);
                constructorChatMessage.setAccessible(true);
                return constructorChatMessage.newInstance(str, new Object[0]);
            }
        }
        catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String stringFromChatComponent(Object chat_component)
    {
        try
        {
            Class<?> classIChatBaseComponent = ReflectionHelper.getNMSClass("IChatBaseComponent");
            Class<?> classIChatBaseComponent$ChatSerializer =
                    ReflectionHelper.getNMSClass("IChatBaseComponent$ChatSerializer");
            Method methodA = ReflectionHelper.getMethod(classIChatBaseComponent$ChatSerializer, "a",
                                                         classIChatBaseComponent);
            methodA.setAccessible(true);
            return (String) methodA.invoke(null, chat_component);
        }
        catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}