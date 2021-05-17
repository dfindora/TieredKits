package com.goldensandsmc.tieredkits.sponge.arrayserializers.raw;

import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public class BooleanArraySerializer implements TypeSerializer<boolean[]>
{
    public BooleanArraySerializer()
    {
    }

    public boolean[] deserialize(@Nonnull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        List<Boolean> list = value.getList(TypeToken.of(Boolean.TYPE), (List<Boolean>) null);
        if (list == null)
        {
            return null;
        }
        else
        {
            boolean[] arr = new boolean[list.size()];
            for (int i = 0; i < list.size(); ++i)
            {
                arr[i] = list.get(i);
            }
            return arr;
        }
    }

    public void serialize(@Nonnull TypeToken<?> type, boolean[] obj, @Nonnull ConfigurationNode value)
            throws ObjectMappingException
    {
        if (obj == null)
        {
            value.setValue(null);
        }
        else
        {
            List<Boolean> list = new ArrayList<>(obj.length);
            for (boolean elem : obj)
            {
                list.add(elem);
            }
            value.setValue(new TypeToken<List<Boolean>>(){}, list);
        }
    }
}
