package com.goldensandsmc.tieredkits.bukkit;

import com.goldensandsmc.tieredkits.JsonConfig;
import com.goldensandsmc.tieredkits.bukkit.commands.EditKitCommand;
import com.goldensandsmc.tieredkits.bukkit.commands.FromJsonCommand;
import com.goldensandsmc.tieredkits.bukkit.commands.KitCommand;
import com.goldensandsmc.tieredkits.bukkit.commands.PreviewKitCommand;
import com.goldensandsmc.tieredkits.bukkit.commands.ToJsonCommand;
import com.goldensandsmc.tieredkits.bukkit.kit.Kit;
import com.goldensandsmc.tieredkits.bukkit.kit.KitUsage;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.shortcircuit.utils.bukkit.command.CommandRegister;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class TieredKits extends JavaPlugin
{
    private final HashMap<String, Kit> kits = new HashMap<>();
    private final HashMap<UUID, HashMap<String, KitUsage>> kit_usage = new HashMap<>();
    private final JsonConfig kits_config;
    private final JsonConfig usage_config;

    public TieredKits()
    {
        this.kits_config =
                new JsonConfig(this, new File(this.getDataFolder() + File.separator + "kits.json"), "kits.json");
        this.usage_config = new JsonConfig(new File(this.getDataFolder() + File.separator + "usage.json"));
        this.kits_config.loadConfig();
        this.usage_config.loadConfig();
    }

    public void onEnable()
    {
        CommandRegister.register(new ToJsonCommand(this));
        CommandRegister.register(new FromJsonCommand(this));
        CommandRegister.register(new KitCommand(this));
        CommandRegister.register(new EditKitCommand(this));
        CommandRegister.register(new PreviewKitCommand(this));
        this.getServer().getPluginManager().registerEvents(new KitPreviewListener(), this);
        Type type = (new TypeToken<HashMap<UUID, HashMap<String, KitUsage>>>()
        {
        }).getType();
        HashMap<UUID, HashMap<String, KitUsage>> new_usages = this.usage_config.getNode("usage", type, new HashMap<>());
        this.kit_usage.clear();
        this.kit_usage.putAll(new_usages);
        this.loadKits();
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        meta.addStoredEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        item.setItemMeta(meta);
        JsonElement serialized = GoodnessGracious.toJson(item);
        ItemStack deserialized = null;

        try
        {
            deserialized = GoodnessGracious.fromJson(serialized, ItemStack.class);
        }
        catch (ReflectiveOperationException var8)
        {
            var8.printStackTrace();
        }

        System.out.println(item);
        System.out.println(serialized);
        System.out.println(deserialized);
    }

    public void onDisable()
    {
        this.usage_config.setNode("usage", this.kit_usage);
        this.usage_config.saveConfig();
    }

    public HashMap<UUID, HashMap<String, KitUsage>> getKitUsage()
    {
        return this.kit_usage;
    }

    public HashMap<String, Kit> getKits()
    {
        return this.kits;
    }

    public void loadKits()
    {
        this.kits_config.loadConfig();
        Type type = (new TypeToken<HashMap<String, Kit>>()
        {
        }).getType();
        HashMap<String, Kit> new_kits = this.kits_config.getNode("kits", type, new HashMap<>());
        this.kits.clear();
        this.kits.putAll(new_kits);
    }

    public void saveKits()
    {
        this.kits_config.setNode("kits", this.kits);
        this.kits_config.saveConfig();
    }
}
