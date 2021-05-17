package com.goldensandsmc.tieredkits.sponge.arrayserializers.raw;

import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public class IntArraySerializer implements TypeSerializer<int[]>
{
    public IntArraySerializer()
    {
    }

    public int[] deserialize(@Nonnull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        List<Integer> list = value.getList(TypeToken.of(Integer.TYPE), (List<Integer>) null);
        if (list == null)
        {
            return null;
        }
        else
        {
            int[] arr = new int[list.size()];

            for (int i = 0; i < list.size(); ++i)
            {
                arr[i] = list.get(i);
            }

            return arr;
        }
    }

    public void serialize(@Nonnull TypeToken<?> type, int[] obj, @Nonnull ConfigurationNode value)
            throws ObjectMappingException
    {
        if (obj == null)
        {
            value.setValue(null);
        }
        else
        {
            List<Integer> list = new ArrayList<>(obj.length);
            for (int elem : obj)
            {
                list.add(elem);
            }
            value.setValue(new TypeToken<List<Integer>>(){}, list);
        }
    }
}
