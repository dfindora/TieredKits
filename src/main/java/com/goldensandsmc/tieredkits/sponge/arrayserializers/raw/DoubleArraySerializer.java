package com.goldensandsmc.tieredkits.sponge.arrayserializers.raw;

import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public class DoubleArraySerializer implements TypeSerializer<double[]>
{
    public DoubleArraySerializer()
    {
    }

    public double[] deserialize(@Nonnull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        List<Double> list = value.getList(TypeToken.of(Double.TYPE), (List<Double>) null);
        if (list == null)
        {
            return null;
        }
        else
        {
            double[] arr = new double[list.size()];
            for (int i = 0; i < list.size(); ++i)
            {
                arr[i] = list.get(i);
            }
            return arr;
        }
    }

    public void serialize(@Nonnull TypeToken<?> type, double[] obj, @Nonnull ConfigurationNode value)
            throws ObjectMappingException
    {
        if (obj == null)
        {
            value.setValue(null);
        }
        else
        {
            List<Double> list = new ArrayList<>(obj.length);
            for (double elem : obj)
            {
                list.add(elem);
            }
            value.setValue(new TypeToken<List<Double>>(){}, list);
        }
    }
}
