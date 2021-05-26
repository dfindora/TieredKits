package com.goldensandsmc.tieredkits.bukkit.adapters.meta;

import com.goldensandsmc.tieredkits.bukkit.adapters.BaseTypeAdapter;
import com.goldensandsmc.tieredkits.bukkit.bukkitreflect.ReflectionHelper;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.server.v1_12_R1.*;

import java.io.IOException;
import java.lang.reflect.Field;

public class NBTBaseTypeAdapter extends TypeAdapter<NBTBase>
{
    @Override
    public void write(JsonWriter jsonWriter, NBTBase nbtBase) throws IOException
    {
        jsonWriter.beginObject();
        jsonWriter.name("typeId").value(nbtBase.getTypeId());
        jsonWriter.name("value");
        Object castedNBTBase = castNBTBase(nbtBase);
        assert castedNBTBase != null;
        BaseTypeAdapter.GSON.toJson(castedNBTBase, castedNBTBase.getClass(), jsonWriter);

        jsonWriter.endObject();
    }

    @Override
    public NBTBase read(JsonReader jsonReader) throws IOException
    {
        if(jsonReader.peek() == JsonToken.NULL)
        {
            return null;
        }
        else
        {
            NBTBase tag = null;
            int typeId = -1;
            jsonReader.beginObject();
            while (jsonReader.hasNext() && jsonReader.peek() != JsonToken.END_OBJECT)
            {
                String tokenName = jsonReader.nextName().toLowerCase();
                switch (tokenName)
                {
                    case "value":
                        switch (typeId)
                        {
                            case 1:
                                tag = new NBTTagByte((byte)jsonReader.nextInt());
                                break;
                            case 2:
                                tag = new NBTTagShort((short)jsonReader.nextInt());
                                break;
                            case 3:
                                tag = new NBTTagInt(jsonReader.nextInt());
                                break;
                            case 4:
                                tag = new NBTTagLong(jsonReader.nextLong());
                                break;
                            case 5:
                                tag = new NBTTagFloat((float)jsonReader.nextDouble());
                                break;
                            case 6:
                                tag = new NBTTagDouble(jsonReader.nextDouble());
                                break;
                            case 8:
                                tag = new NBTTagString(jsonReader.nextString());
                                break;
                        }
                        break;
                    case "typeid":
                        typeId = jsonReader.nextInt();
                        break;
                }
            }
            jsonReader.endObject();
            return tag;
        }
    }

    private Object castNBTBase(NBTBase tag)
    {
        String type = NBTBase.j(tag.getTypeId());
        String substring = tag.toString().substring(0, tag.toString().length() - 1);
        switch (type)
        {
            case "TAG_Byte":
                return Byte.parseByte(substring);
            case "TAG_Short":
                return Short.parseShort(substring);
            case "TAG_Int":
                return Integer.parseInt(tag.toString());
            case "TAG_Long":
                return Long.parseLong(substring);
            case "TAG_Float":
                return Float.parseFloat(substring);
            case "TAG_Double":
                return Double.parseDouble(substring);
            case "TAG_Byte_Array":
                return parseArray(tag.toString(), Byte.class);
            case "TAG_String":
                return (tag.toString().charAt(0) == '\"' && tag.toString().charAt(tag.toString().length()) == '\"')
                       ? tag.toString().substring(1,tag.toString().length() - 1) : tag.toString();
            case "TAG_Int_Array":
                return parseArray(tag.toString(), Integer.class);
            case "TAG_Long_Array":
                return parseArray(tag.toString(), Long.class);
            case "TAG_End":
                return tag.toString();
            case "TAG_List":
                try
                {
                    Field nbtList = ReflectionHelper.getField(ReflectionHelper.getNMSClass("NBTTagList"), "list");
                    nbtList.setAccessible(true);
                    return nbtList.get(tag);
                }
                catch (ReflectiveOperationException e)
                {
                    e.printStackTrace();
                    return null;
                }
            case "TAG_Compound":
                try
                {
                    Field nbtMap = ReflectionHelper.getField(ReflectionHelper.getNMSClass("NBTTagCompound"), "map");
                    nbtMap.setAccessible(true);
                    return nbtMap.get(tag);
                }
                catch (ReflectiveOperationException e)
                {
                    e.printStackTrace();
                    return null;
                }
            default:
                return null;
        }
    }

    private Object[] parseArray(String arrayString, Class<?> type)
    {
        arrayString = arrayString.substring(3, arrayString.length() - 1);
        arrayString = arrayString.replaceAll("b", "");
        String[] substringedArray = arrayString.split(",");
        Object[] byteArr = new Byte[substringedArray.length];
        Object[] intArr = new Integer[substringedArray.length];
        Object[] longArr = new Long[substringedArray.length];
        for(int i = 0; i < substringedArray.length; i++)
        {
            if(type == Byte.class)
            {
                byteArr[i] = Byte.parseByte(substringedArray[i]);
            }
            else if(type == Integer.class)
            {
                intArr[i] = Integer.parseInt(substringedArray[i]);
            }
            else if(type == Long.class)
            {
                longArr[i] = Long.parseLong(substringedArray[i]);
            }
        }
        if(type == Byte.class)
        {
            return byteArr;
        }
        else if(type == Integer.class)
        {
            return intArr;
        }
        else if(type == Long.class)
        {
            return longArr;
        }
        else
        {
            return null;
        }
    }
}
