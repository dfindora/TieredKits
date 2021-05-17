package com.goldensandsmc.tieredkits.sponge.arrayserializers.raw;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public class CharacterSerializer implements TypeSerializer<Character>
{
    public CharacterSerializer()
    {
    }

    public Character deserialize(@Nonnull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        return value.getString() != null && !value.getString().isEmpty() ? value.getString().charAt(0) : null;
    }

    public void serialize(@Nonnull TypeToken<?> type, Character obj, @Nonnull ConfigurationNode value)
            throws ObjectMappingException
    {
        if (obj == null)
        {
            value.setValue(null);
        }
        else
        {
            value.setValue(obj.toString());
        }
    }
}
