package com.goldensandsmc.tieredkits.sponge.arrayserializers.raw;

import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public class FloatArraySerializer implements TypeSerializer<float[]>
{
    public FloatArraySerializer()
    {
    }

    public float[] deserialize(@Nonnull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        List<Float> list = value.getList(TypeToken.of(Float.TYPE), (List<Float>) null);
        if (list == null)
        {
            return null;
        }
        else
        {
            float[] arr = new float[list.size()];
            for (int i = 0; i < list.size(); ++i)
            {
                arr[i] = list.get(i);
            }
            return arr;
        }
    }

    public void serialize(@Nonnull TypeToken<?> type, float[] obj, @Nonnull ConfigurationNode value)
            throws ObjectMappingException
    {
        if (obj == null)
        {
            value.setValue(null);
        }
        else
        {
            List<Float> list = new ArrayList<>(obj.length);
            for (float elem : obj)
            {
                list.add(elem);
            }
            value.setValue(new TypeToken<List<Float>>(){}, list);
        }
    }
}
