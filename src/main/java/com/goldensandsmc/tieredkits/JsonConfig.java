package com.goldensandsmc.tieredkits;

import com.google.gson.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

import org.apache.commons.lang3.reflect.TypeUtils;

public class JsonConfig
{
    private final ClassLoader context;
    private final File file;
    private final String defaultPath;
    private JsonObject root;
    private final HashMap<String, Object> valueCache;

    public JsonConfig(File file)
    {
        this(null, file, null);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public JsonConfig(Object context, File file, String defaultPath)
    {
        this.valueCache = new HashMap<>();
        this.file = file;
        file.getParentFile().mkdirs();
        this.defaultPath = defaultPath;
        this.context = context == null ? null : context.getClass().getClassLoader();
        this.saveDefaultConfig();
        this.loadConfig();
    }

    public synchronized void loadConfig() throws JsonSyntaxException
    {
        synchronized (this)
        {
            try
            {
                if (!this.file.exists())
                {
                    throw new IllegalStateException("Configuration file not found");
                }

                StringBuilder builder = new StringBuilder();
                Scanner scanner = new Scanner(this.file);

                String data;
                while (scanner.hasNextLine())
                {
                    data = scanner.nextLine();
                    builder.append(data).append('\n');
                }

                scanner.close();
                data = builder.toString().replaceAll("(/\\*(.*)\\*/)|(//(.*))", "");
                this.valueCache.clear();
                JsonElement element = (new JsonParser()).parse(data);
                if (element instanceof JsonObject)
                {
                    this.root = element.getAsJsonObject();
                }
                else
                {
                    this.root = new JsonObject();
                }
            }
            catch (FileNotFoundException fileNotFoundException)
            {
                fileNotFoundException.printStackTrace();
            }

        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public synchronized void saveConfig()
    {
        synchronized (this)
        {
            try
            {
                this.file.createNewFile();
                FileWriter writer = new FileWriter(this.file);
                writer.write(JsonUtils.getGson().toJson(this.root));
                writer.flush();
                writer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public synchronized void saveDefaultConfig()
    {
        synchronized (this)
        {
            this.saveDefaultConfig(false);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public synchronized void saveDefaultConfig(boolean overwrite)
    {
        synchronized (this)
        {
            if (!this.file.exists() || overwrite)
            {
                try
                {
                    this.file.createNewFile();
                    if (this.context != null && this.defaultPath != null)
                    {
                        Files.copy(Objects.requireNonNull(this.context.getResourceAsStream(this.defaultPath)),
                                   this.file.toPath(),
                                   StandardCopyOption.REPLACE_EXISTING);
                    }
                    else
                    {
                        this.saveConfig();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    public synchronized <T> void setNode(String path, T value)
    {
        synchronized (this)
        {
            JsonObject curPath = this.root;
            String[] nodes = path.split("\\.");

            for (int i = 0; i < nodes.length - 1; ++i)
            {
                if (!curPath.has(nodes[i]))
                {
                    curPath.add(nodes[i], new JsonObject());
                }

                curPath = curPath.getAsJsonObject(nodes[i]);
            }

            curPath.add(nodes[nodes.length - 1], JsonUtils.getGson().toJsonTree(value));
            this.valueCache.put(path, value);
        }
    }

    public <T> T getNode(String path, Type type, T defaultIfNull)
    {
        synchronized (this)
        {
            if (this.valueCache.containsKey(path))
            {
                Object value = this.valueCache.get(path);
                if (value == null || TypeUtils.isAssignable(value.getClass(), type))
                {
                    @SuppressWarnings("unchecked") T result = (T) value;
                    return result;
                }
            }

            JsonObject curPath = this.root;
            String[] nodes = path.split("\\.");

            for (int i = 0; i < nodes.length - 1; ++i)
            {
                if (!curPath.has(nodes[i]))
                {
                    if (defaultIfNull == null)
                    {
                        this.valueCache.put(path, null);
                        return null;
                    }

                    curPath.add(nodes[i], new JsonObject());
                }

                curPath = curPath.getAsJsonObject(nodes[i]);
            }

            if (!curPath.has(nodes[nodes.length - 1]) && defaultIfNull != null)
            {
                curPath.add(nodes[nodes.length - 1], JsonUtils.getGson().toJsonTree(defaultIfNull, type));
            }

            JsonElement rawValue = curPath.get(nodes[nodes.length - 1]);
            T value = rawValue != null && !(rawValue instanceof JsonNull) ? JsonUtils.getGson()
                                                                                       .fromJson(rawValue, type)
                                                                            : defaultIfNull;
            this.valueCache.put(path, value);
            return value;
        }
    }

    @SuppressWarnings("unused")
    public <T> T getNode(String node, Class<T> type, T defaultIfNull)
    {
        return this.getNode(node, (Type) type, defaultIfNull);
    }
}
