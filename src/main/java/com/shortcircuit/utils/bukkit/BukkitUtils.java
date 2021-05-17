package com.shortcircuit.utils.bukkit;

import com.shortcircuit.utils.Version;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class BukkitUtils
{
    private static final HashSet<Material> TRANSPARENT_BLOCKS = getTransparentBlocks();
    private static final HashSet<Byte> LEGACY_TRANSPARENT_BLOCKS = getLegacyTransparentBlocks();
    private static final Boolean title_supported = isTitleSupported();

    public BukkitUtils()
    {
    }

    public static boolean isTitleSupported()
    {
        if (title_supported == null)
        {
            return (new Version(Bukkit.getBukkitVersion().split("\\-")[0])).compareTo(new Version(1, 8, 8)) >= 0;
        }
        else
        {
            return title_supported;
        }
    }

    public static HashSet<Byte> getLegacyTransparentBlocks()
    {
        if (LEGACY_TRANSPARENT_BLOCKS != null)
        {
            return LEGACY_TRANSPARENT_BLOCKS;
        }
        else
        {
            HashSet<Byte> legacy_transparent_blocks = new HashSet<>();
            HashSet<Material> transparent_blocks = getTransparentBlocks();

            for (Material material : transparent_blocks)
            {
                legacy_transparent_blocks.add((byte) material.getId());
            }

            return legacy_transparent_blocks;
        }
    }

    public static HashSet<Material> getTransparentBlocks()
    {
        if (TRANSPARENT_BLOCKS == null)
        {
            HashSet<Material> blocks = new HashSet<>();
            for (Material material : Material.values())
            {
                if (material.isTransparent())
                {
                    blocks.add(material);
                }
            }
            return blocks;
        }
        else
        {
            return TRANSPARENT_BLOCKS;
        }
    }

    public static void addTransparentBlock(String material_name)
    {
        Material material = Material.valueOf(material_name);
        TRANSPARENT_BLOCKS.add(material);
        LEGACY_TRANSPARENT_BLOCKS.add((byte) material.getId());
    }

    public static Location getNeighboringBlock(Location origin, BlockFace... faces)
    {
        int modX = 0;
        int modY = 0;
        int modZ = 0;
        for (BlockFace face : faces)
        {
            modX += face.getModX();
            modY += face.getModY();
            modZ += face.getModZ();
        }

        return origin.add(modX, modY, modZ);
    }
}
