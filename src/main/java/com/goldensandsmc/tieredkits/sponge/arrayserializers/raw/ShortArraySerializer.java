package com.goldensandsmc.tieredkits.sponge.arrayserializers.raw;

import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public class ShortArraySerializer implements TypeSerializer<short[]>
{
    public ShortArraySerializer()
    {
    }

    public short[] deserialize(@Nonnull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        List<Short> list = value.getList(TypeToken.of(Short.TYPE), (List<Short>) null);
        if (list == null)
        {
            return null;
        }
        else
        {
            short[] arr = new short[list.size()];
            for (int i = 0; i < list.size(); ++i)
            {
                arr[i] = list.get(i);
            }
            return arr;
        }
    }

    public void serialize(@Nonnull TypeToken<?> type, short[] obj, @Nonnull ConfigurationNode value)
            throws ObjectMappingException
    {
        if (obj == null)
        {
            value.setValue(null);
        }
        else
        {
            List<Short> list = new ArrayList<>(obj.length);
            for (short elem : obj)
            {
                list.add(elem);
            }
            value.setValue(new TypeToken<List<Short>>(){}, list);
        }
    }
}
