package com.goldensandsmc.tieredkits.sponge.arrayserializers.raw;

import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public class ByteArraySerializer implements TypeSerializer<byte[]>
{
    public ByteArraySerializer()
    {
    }

    public byte[] deserialize(@Nonnull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        List<Byte> list = value.getList(TypeToken.of(Byte.TYPE), (List<Byte>) null);
        if (list == null)
        {
            return null;
        }
        else
        {
            byte[] arr = new byte[list.size()];
            for (int i = 0; i < list.size(); ++i)
            {
                arr[i] = list.get(i);
            }
            return arr;
        }
    }

    public void serialize(@Nonnull TypeToken<?> type, byte[] obj, @Nonnull ConfigurationNode value)
            throws ObjectMappingException
    {
        if (obj == null)
        {
            value.setValue(null);
        }
        else
        {
            List<Byte> list = new ArrayList<>(obj.length);
            for (byte elem : obj)
            {
                list.add(elem);
            }
            value.setValue(new TypeToken<List<Byte>>(){}, list);
        }
    }
}
