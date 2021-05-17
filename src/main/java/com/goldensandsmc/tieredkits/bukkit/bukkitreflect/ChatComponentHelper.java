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
            Class<?> class_IChatBaseComponent$ChatSerializer =
                    ReflectionHelper.getNMSClass("IChatBaseComponent$ChatSerializer");
            Method method_a = ReflectionHelper.getMethod(class_IChatBaseComponent$ChatSerializer, "a", String.class);
            method_a.setAccessible(true);

            try
            {
                return method_a.invoke(null, str);
            }
            catch (Exception e)
            {
                Class<?> class_ChatMessage = ReflectionHelper.getNMSClass("ChatMessage");
                Constructor<?> constructor_ChatMessage =
                        ReflectionHelper.getConstructor(class_ChatMessage, String.class,
                                                        Object[].class);
                constructor_ChatMessage.setAccessible(true);
                return constructor_ChatMessage.newInstance(str, new Object[0]);
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
            Class<?> class_IChatBaseComponent = ReflectionHelper.getNMSClass("IChatBaseComponent");
            Class<?> class_IChatBaseComponent$ChatSerializer =
                    ReflectionHelper.getNMSClass("IChatBaseComponent$ChatSerializer");
            Method method_a = ReflectionHelper.getMethod(class_IChatBaseComponent$ChatSerializer, "a",
                                                         class_IChatBaseComponent);
            method_a.setAccessible(true);
            return (String) method_a.invoke(null, chat_component);
        }
        catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}