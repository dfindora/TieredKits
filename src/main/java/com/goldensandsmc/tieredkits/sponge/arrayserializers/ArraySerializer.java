package com.goldensandsmc.tieredkits.sponge.arrayserializers;

import com.goldensandsmc.tieredkits.sponge.arrayserializers.raw.BooleanArraySerializer;
import com.goldensandsmc.tieredkits.sponge.arrayserializers.raw.ByteArraySerializer;
import com.goldensandsmc.tieredkits.sponge.arrayserializers.raw.CharArraySerializer;
import com.goldensandsmc.tieredkits.sponge.arrayserializers.raw.CharacterSerializer;
import com.goldensandsmc.tieredkits.sponge.arrayserializers.raw.DoubleArraySerializer;
import com.goldensandsmc.tieredkits.sponge.arrayserializers.raw.FloatArraySerializer;
import com.goldensandsmc.tieredkits.sponge.arrayserializers.raw.IntArraySerializer;
import com.goldensandsmc.tieredkits.sponge.arrayserializers.raw.LongArraySerializer;
import com.goldensandsmc.tieredkits.sponge.arrayserializers.raw.ShortArraySerializer;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public class ArraySerializer<T> implements TypeSerializer<T[]>
{
    private final TypeToken<T> inferred_type;
    private final TypeToken<List<T>> inferred_list_type;

    public ArraySerializer(TypeToken<T> component_type, TypeToken<List<T>> list_type)
    {
        this.inferred_type = component_type;
        this.inferred_list_type = list_type;
    }

    public T[] deserialize(@Nonnull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        List<T> list = value.getList(this.inferred_type, (List<T>) null);
        if (list == null)
        {
            return null;
        }
        else
        {
            T[] arr = (T[]) Array.newInstance(this.inferred_type.getRawType(), list.size());
            list.toArray(arr);
            return arr;
        }
    }

    public void serialize(@Nonnull TypeToken<?> type, T[] obj, @Nonnull ConfigurationNode value)
            throws ObjectMappingException
    {
        if (obj == null)
        {
            value.setValue(null);
        }
        else
        {
            List<T> list = Lists.newArrayList(obj);
            value.setValue(this.inferred_list_type, list);
        }
    }

    public static TypeSerializerCollection registerStandardArraySerializers(TypeSerializerCollection serializers)
    {
        serializers.registerType(TypeToken.of(Character.TYPE), new CharacterSerializer());
        serializers.registerType(TypeToken.of(Character.class), new CharacterSerializer());
        serializers.registerType(TypeToken.of(Boolean[].class),
                                 new ArraySerializer<>(TypeToken.of(Boolean.class), new TypeToken<List<Boolean>>()
                                 {
                                 }));
        serializers.registerType(TypeToken.of(Byte[].class),
                                 new ArraySerializer<>(TypeToken.of(Byte.class), new TypeToken<List<Byte>>()
                                 {
                                 }));
        serializers.registerType(TypeToken.of(Short[].class),
                                 new ArraySerializer<>(TypeToken.of(Short.class), new TypeToken<List<Short>>()
                                 {
                                 }));
        serializers.registerType(TypeToken.of(Character[].class),
                                 new ArraySerializer<>(TypeToken.of(Character.class), new TypeToken<List<Character>>()
                                 {
                                 }));
        serializers.registerType(TypeToken.of(Integer[].class),
                                 new ArraySerializer<>(TypeToken.of(Integer.class), new TypeToken<List<Integer>>()
                                 {
                                 }));
        serializers.registerType(TypeToken.of(Float[].class),
                                 new ArraySerializer<>(TypeToken.of(Float.class), new TypeToken<List<Float>>()
                                 {
                                 }));
        serializers.registerType(TypeToken.of(Long[].class),
                                 new ArraySerializer<>(TypeToken.of(Long.class), new TypeToken<List<Long>>()
                                 {
                                 }));
        serializers.registerType(TypeToken.of(Double[].class),
                                 new ArraySerializer<>(TypeToken.of(Double.class), new TypeToken<List<Double>>()
                                 {
                                 }));
        serializers.registerType(TypeToken.of(String[].class),
                                 new ArraySerializer<>(TypeToken.of(String.class), new TypeToken<List<String>>()
                                 {
                                 }));
        serializers.registerType(TypeToken.of(boolean[].class), new BooleanArraySerializer());
        serializers.registerType(TypeToken.of(byte[].class), new ByteArraySerializer());
        serializers.registerType(TypeToken.of(short[].class), new ShortArraySerializer());
        serializers.registerType(TypeToken.of(char[].class), new CharArraySerializer());
        serializers.registerType(TypeToken.of(int[].class), new IntArraySerializer());
        serializers.registerType(TypeToken.of(float[].class), new FloatArraySerializer());
        serializers.registerType(TypeToken.of(long[].class), new LongArraySerializer());
        serializers.registerType(TypeToken.of(double[].class), new DoubleArraySerializer());
        return serializers;
    }
}
