package com.shortcircuit.utils.bukkit;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class PlayerUtils
{
    public PlayerUtils()
    {
    }

    public static BlockFace getCardinalDirection(Location loc)
    {
        double rotation = ((double) loc.getYaw() + 180.0D) % 360.0D;
        if (rotation < 0.0D)
        {
            rotation += 360.0D;
        }

        if (0.0D <= rotation && rotation < 22.5D)
        {
            return BlockFace.NORTH;
        }
        else if (22.5D <= rotation && rotation < 67.5D)
        {
            return BlockFace.NORTH_EAST;
        }
        else if (67.5D <= rotation && rotation < 112.5D)
        {
            return BlockFace.EAST;
        }
        else if (112.5D <= rotation && rotation < 157.5D)
        {
            return BlockFace.SOUTH_EAST;
        }
        else if (157.5D <= rotation && rotation < 202.5D)
        {
            return BlockFace.SOUTH;
        }
        else if (202.5D <= rotation && rotation < 247.5D)
        {
            return BlockFace.SOUTH_WEST;
        }
        else if (247.5D <= rotation && rotation < 292.5D)
        {
            return BlockFace.WEST;
        }
        else if (292.5D <= rotation && rotation < 337.5D)
        {
            return BlockFace.NORTH_WEST;
        }
        else
        {
            return 337.5D <= rotation && rotation < 360.0D ? BlockFace.NORTH : BlockFace.DOWN;
        }
    }
}
