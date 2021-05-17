package com.goldensandsmc.tieredkits.bukkit.bukkitreflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;

public class ReflectionHelper
{
    public static final Package CRAFTBUKKIT_PACKAGE;
    public static final Package NMS_PACKAGE;

    public ReflectionHelper()
    {
    }

    public static Class<?> getCraftbukkitClass(String name) throws ClassNotFoundException
    {
        return Class.forName(CRAFTBUKKIT_PACKAGE.getName() + "." + name);
    }

    public static Class<?> getNMSClass(String name) throws ClassNotFoundException
    {
        return Class.forName(NMS_PACKAGE.getName() + "." + name);
    }

    public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException
    {
        try
        {
            return clazz.getDeclaredField(name);
        }
        catch (NoSuchFieldException var3)
        {
            if (clazz.getSuperclass() != null)
            {
                return getField(clazz.getSuperclass(), name);
            }
            else
            {
                throw new NoSuchFieldException(name);
            }
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameter_types) throws
            NoSuchMethodException
    {
        try
        {
            return clazz.getDeclaredMethod(name, parameter_types);
        }
        catch (NoSuchMethodException var4)
        {
            if (clazz.getSuperclass() != null)
            {
                return getMethod(clazz.getSuperclass(), name, parameter_types);
            }
            else
            {
                throw new NoSuchMethodException(name);
            }
        }
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameter_types) throws
            NoSuchMethodException
    {
        return clazz.getDeclaredConstructor(parameter_types);
    }

    public static Field[] getAllFields(Class<?> clazz)
    {
        LinkedList<Field> fields = new LinkedList<>();
        getAllFields(clazz, fields);
        return fields.toArray(new Field[0]);
    }

    private static void getAllFields(Class<?> clazz, List<Field> fields)
    {
        Collections.addAll(fields, clazz.getDeclaredFields());
        if (clazz.getSuperclass() != null)
        {
            getAllFields(clazz.getSuperclass(), fields);
        }

    }

    public static Method[] getAllMethods(Class<?> clazz)
    {
        LinkedList<Method> fields = new LinkedList<>();
        getAllMethods(clazz, fields);
        return fields.toArray(new Method[0]);
    }

    private static void getAllMethods(Class<?> clazz, List<Method> methods)
    {
        Collections.addAll(methods, clazz.getDeclaredMethods());
        if (clazz.getSuperclass() != null)
        {
            getAllMethods(clazz.getSuperclass(), methods);
        }

    }

    static
    {
        Class<? extends Server> cb_server_class = Bukkit.getServer().getClass();
        Package temp = null;

        try
        {
            Method methodHandle = cb_server_class.getDeclaredMethod("getServer");
            methodHandle.setAccessible(true);
            Class<?> nms_server_class = methodHandle.invoke(Bukkit.getServer()).getClass();
            temp = nms_server_class.getPackage();
        }
        catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
        }

        NMS_PACKAGE = temp;
        System.out.println("NMS_PACKAGE = " + NMS_PACKAGE);
        CRAFTBUKKIT_PACKAGE = cb_server_class.getPackage();
        System.out.println("CRAFTBUKKIT_PACKAGE = " + CRAFTBUKKIT_PACKAGE);
    }
}
