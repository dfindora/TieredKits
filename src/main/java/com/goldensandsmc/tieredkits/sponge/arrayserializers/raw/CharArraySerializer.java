package com.goldensandsmc.tieredkits.sponge.arrayserializers.raw;

import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public class CharArraySerializer implements TypeSerializer<char[]>
{
    public CharArraySerializer()
    {
    }

    public char[] deserialize(@Nonnull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        List<Character> list = value.getList(TypeToken.of(Character.class), (List<Character>) null);
        if (list == null)
        {
            return null;
        }
        else
        {
            char[] arr = new char[list.size()];
            for (int i = 0; i < list.size(); ++i)
            {
                arr[i] = list.get(i);
            }
            return arr;
        }
    }

    public void serialize(@Nonnull TypeToken<?> type, char[] obj, @Nonnull ConfigurationNode value)
            throws ObjectMappingException
    {
        if (obj == null)
        {
            value.setValue(null);
        }
        else
        {
            List<Character> list = new ArrayList<>(obj.length);
            for (char elem : obj)
            {
                list.add(elem);
            }
            value.setValue(new TypeToken<List<Character>>(){}, list);
        }
    }
}
