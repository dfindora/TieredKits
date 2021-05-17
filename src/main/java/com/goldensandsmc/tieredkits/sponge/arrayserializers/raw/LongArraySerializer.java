package com.goldensandsmc.tieredkits.sponge.arrayserializers.raw;

import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public class LongArraySerializer implements TypeSerializer<long[]>
{
    public LongArraySerializer()
    {
    }

    public long[] deserialize(@Nonnull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        List<Long> list = value.getList(TypeToken.of(Long.TYPE), (List<Long>) null);
        if (list == null)
        {
            return null;
        }
        else
        {
            long[] arr = new long[list.size()];
            for (int i = 0; i < list.size(); ++i)
            {
                arr[i] = list.get(i);
            }
            return arr;
        }
    }

    public void serialize(@Nonnull TypeToken<?> type, long[] obj, @Nonnull ConfigurationNode value)
            throws ObjectMappingException
    {
        if (obj == null)
        {
            value.setValue(null);
        }
        else
        {
            List<Long> list = new ArrayList<>(obj.length);
            for (long elem : obj)
            {
                list.add(elem);
            }
            value.setValue(new TypeToken<List<Long>>(){}, list);
        }
    }
}
