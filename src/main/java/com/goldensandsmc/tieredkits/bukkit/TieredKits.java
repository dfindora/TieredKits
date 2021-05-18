package com.goldensandsmc.tieredkits.bukkit;

import com.goldensandsmc.tieredkits.JsonConfig;
import com.goldensandsmc.tieredkits.bukkit.commands.*;
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
    private final HashMap<UUID, HashMap<String, KitUsage>> kitUsage = new HashMap<>();
    private final JsonConfig kitsConfig;
    private final JsonConfig usageConfig;

    public TieredKits()
    {
        this.kitsConfig =
                new JsonConfig(this, new File(this.getDataFolder() + File.separator + "kits.json"), "kits.json");
        this.usageConfig = new JsonConfig(new File(this.getDataFolder() + File.separator + "usage.json"));
        this.kitsConfig.loadConfig();
        this.usageConfig.loadConfig();
    }

    public void onEnable()
    {
        CommandRegister.register(new ToJsonCommand(this));
        CommandRegister.register(new FromJsonCommand(this));
        CommandRegister.register(new KitCommand(this));
        CommandRegister.register(new EditKitCommand(this));
        CommandRegister.register(new PreviewKitCommand(this));
        CommandRegister.register(new ResetKitCommand(this));
        CommandRegister.register(new ReloadKitCommand(this));
        CommandRegister.register(new CreateKitCommand(this));
        CommandRegister.register(new RemoveKitCommand(this));
        this.getServer().getPluginManager().registerEvents(new KitPreviewListener(), this);
        this.loadUsages();
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
        catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
        }

        System.out.println(item);
        System.out.println(serialized);
        System.out.println(deserialized);
    }

    public void onDisable()
    {
        saveUsages();
    }

    public HashMap<UUID, HashMap<String, KitUsage>> getKitUsage()
    {
        return this.kitUsage;
    }

    public HashMap<String, Kit> getKits()
    {
        return this.kits;
    }

    public void loadUsages()
    {
        this.usageConfig.loadConfig();
        Type type = (new TypeToken<HashMap<UUID, HashMap<String, KitUsage>>>(){}).getType();
        HashMap<UUID, HashMap<String, KitUsage>> newUsages = this.usageConfig.getNode("usage", type, new HashMap<>());
        this.kitUsage.clear();
        this.kitUsage.putAll(newUsages);
    }

    public void saveUsages()
    {
        this.usageConfig.setNode("usage", this.kitUsage);
        this.usageConfig.saveConfig();
    }

    public void loadKits()
    {
        this.kitsConfig.loadConfig();
        Type type = (new TypeToken<HashMap<String, Kit>>(){}).getType();
        HashMap<String, Kit> newKits = this.kitsConfig.getNode("kits", type, new HashMap<>());
        this.kits.clear();
        this.kits.putAll(newKits);
    }

    public void saveKits()
    {
        this.kitsConfig.setNode("kits", this.kits);
        this.kitsConfig.saveConfig();
    }
}
