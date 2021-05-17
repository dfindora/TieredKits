package com.goldensandsmc.tieredkits.sponge;

import com.goldensandsmc.tieredkits.sponge.arrayserializers.ArraySerializer;
import com.goldensandsmc.tieredkits.sponge.commands.EditKitCommand;
import com.goldensandsmc.tieredkits.sponge.commands.KitCommand;
import com.goldensandsmc.tieredkits.sponge.commands.PreviewKitCommand;
import com.goldensandsmc.tieredkits.sponge.kit.Kit;
import com.goldensandsmc.tieredkits.sponge.kit.KitUsage;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "tieredkits",
        name = "TieredKits",
        version = "1.0",
        description = "Kits with different levels of rewards"
)
@SuppressWarnings("UnstableApiUsage")
public class TieredKits
{
    private static TieredKits instance;
    public static final String PLUGIN_IDENTIFIER = "tieredkits";
    private final HashMap<String, Kit> kits = new HashMap<>();
    private final TypeToken<HashMap<String, Kit>> kits_type = new TypeToken<HashMap<String, Kit>>()
    {
    };
    private final HashMap<UUID, HashMap<String, KitUsage>> kit_usage = new HashMap<>();
    private final TypeToken<HashMap<UUID, HashMap<String, KitUsage>>> kit_usage_type =
            new TypeToken<HashMap<UUID, HashMap<String, KitUsage>>>()
            {
            };
    private final ConfigurationLoader<CommentedConfigurationNode> kits_loader;
    private final ConfigurationLoader<CommentedConfigurationNode> usage_loader;
    private CommentedConfigurationNode kits_node;
    private CommentedConfigurationNode usage_node;
    @Inject
    private Logger logger;

    public TieredKits()
    {
        instance = this;
        Path private_config_dir = (new File("tieredkits")).toPath();
        private_config_dir.toFile().mkdirs();
        this.kits_loader =
                HoconConfigurationLoader.builder().setPath(private_config_dir.resolve("kits.conf"))
                        .build();
        this.kits_loader.getDefaultOptions().setSerializers(ArraySerializer.registerStandardArraySerializers(
                this.kits_loader.getDefaultOptions().getSerializers()));
        this.usage_loader =
                HoconConfigurationLoader.builder().setPath(private_config_dir.resolve("usage.conf"))
                        .build();
    }

    public void loadConfig() throws IOException, ObjectMappingException
    {
        this.kits_node = this.kits_loader.load();
        Map<String, Kit> temp_kits = this.kits_node.getValue(this.kits_type);
        if (temp_kits != null)
        {
            this.kits.clear();
            this.kits.putAll(temp_kits);
            this.logger.info("Kits loaded");
        }

        this.usage_node = this.usage_loader.load();
        Map<UUID, HashMap<String, KitUsage>> temp_usage = this.usage_node.getValue(this.kit_usage_type);
        if (temp_usage != null)
        {
            this.kit_usage.clear();
            this.kit_usage.putAll(temp_usage);
            this.logger.info("Usage loaded");
        }

    }

    public void saveConfig() throws IOException, ObjectMappingException
    {
        if (this.kits_node == null)
        {
            this.kits_node = this.kits_loader.createEmptyNode();
        }

        this.kits_node.setValue(this.kits_type, this.kits);
        this.kits_loader.save(this.kits_node);
        this.logger.info("Kits saved");
        if (this.usage_node == null)
        {
            this.usage_node = this.usage_loader.createEmptyNode();
        }

        this.usage_node.setValue(this.kit_usage_type, this.kit_usage);
        this.usage_loader.save(this.usage_node);
        this.logger.info("Usage saved");
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event)
    {
        try
        {
            this.loadConfig();
        }
        catch (ObjectMappingException | IOException var3)
        {
            var3.printStackTrace();
        }

        new PreviewKitCommand(this);
        new EditKitCommand(this);
        new KitCommand(this);
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event)
    {
        try
        {
            this.saveConfig();
        }
        catch (ObjectMappingException | IOException var3)
        {
            var3.printStackTrace();
        }

    }

    public static TieredKits getInstance()
    {
        return instance;
    }

    public Logger getLogger()
    {
        return this.logger;
    }

    public HashMap<String, Kit> getKits()
    {
        return this.kits;
    }

    public HashMap<UUID, HashMap<String, KitUsage>> getKitUsage()
    {
        return this.kit_usage;
    }
}
