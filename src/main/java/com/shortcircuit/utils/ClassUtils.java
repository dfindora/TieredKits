package com.shortcircuit.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ClassUtils
{
    public ClassUtils()
    {
    }

    public static StackTraceElement getCallerTrace()
    {
        return Thread.currentThread().getStackTrace()[3];
    }

    public static StackTraceElement getCallerTrace(int level)
    {
        return Thread.currentThread().getStackTrace()[level];
    }

    public static <T> T cast(Object obj, Class<T> clazz)
    {
        if (obj == null)
        {
            return null;
        }
        else
        {
            try
            {
                if (obj instanceof Long && !clazz.equals(Long.class))
                {
                    Long lobj = (Long) obj;
                    Integer iobj = lobj.intValue();
                    return clazz.cast(iobj);
                }
                else
                {
                    return clazz.cast(obj);
                }
            }
            catch (ClassCastException e)
            {
                if (clazz.equals(String.class))
                {
                    return (T) obj.toString();
                }
                else
                {
                    throw e;
                }
            }
        }
    }

    public static <T> List<T> castList(List<?> list, Class<T> clazz)
    {
        if (list == null)
        {
            return null;
        }
        else
        {
            Object cast;
            try
            {
                Class<?> list_class = list.getClass();

                try
                {
                    cast = list_class.getDeclaredConstructor(Integer.TYPE).newInstance(list.size());
                }
                catch (NoSuchMethodException var5)
                {
                    cast = list_class.getDeclaredConstructor().newInstance();
                }
            }
            catch (ReflectiveOperationException var6)
            {
                cast = new ArrayList<>(list.size());
            }

            for (Object obj : list)
            {
                ((List<T>) cast).add(cast(obj, clazz));
            }

            return (List<T>) cast;
        }
    }

    public static <T extends Collection<?>, E, C extends Collection<E>> C castCollection(T collection, Class<E> type_class)
    {
        return (C) castCollection(collection, type_class, collection.getClass());
    }

    public static <T extends Collection<?>, E, C extends Collection<E>> C castCollection(T collection, Class<E> type_class, Class<C> set_class)
    {
        Collection cast;
        try
        {
            try
            {
                cast = set_class.getDeclaredConstructor(Integer.TYPE).newInstance(collection.size());
            }
            catch (NoSuchMethodException var6)
            {
                cast = set_class.getDeclaredConstructor().newInstance();
            }
        }
        catch (ReflectiveOperationException var7)
        {
            return null;
        }

        for (Object element : collection)
        {
            cast.add(cast(element, type_class));
        }

        return (C) cast;
    }

    public static String formatStackTrace(StackTraceElement element)
    {
        return element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element
                .getLineNumber() + ")";
    }

    public static String getStackTrace(Throwable throwable)
    {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
