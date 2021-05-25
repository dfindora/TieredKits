package com.goldensandsmc.tieredkits.bukkit.adapters.meta;

import com.goldensandsmc.tieredkits.bukkit.adapters.BaseTypeAdapter;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.server.v1_12_R1.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
        switch (type)
        {
            case "TAG_Byte":
                return Byte.parseByte(tag.toString());
            case "TAG_Short":
                return Short.parseShort(tag.toString());
            case "TAG_Int":
                return Integer.parseInt(tag.toString());
            case "TAG_Long":
                return Long.parseLong(tag.toString());
            case "TAG_Float":
                return Float.parseFloat(tag.toString());
            case "TAG_Double":
                return Double.parseDouble(tag.toString());
            case "TAG_Byte_Array":
                return tag.toString().getBytes(StandardCharsets.UTF_8);
            case "TAG_String":
                return tag.toString().substring(1,tag.toString().length() - 1);
            case "TAG_End":
            case "TAG_List":
            case "TAG_Compound":
            case "TAG_Int_Array":
            case "TAG_Long_Array":
            default:
                return null;
        }
    }
}
