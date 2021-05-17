package com.shortcircuit.utils.bukkit.command;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

public class PermissionUtils
{
    public PermissionUtils()
    {
    }

    public static boolean hasPermission(Permissible user, String permission)
    {
        if (user.hasPermission(permission))
        {
            return true;
        }
        else
        {
            String[] parts = permission.split("\\.");
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < parts.length - 1; ++i)
            {
                builder.append(parts[i]).append('.');
                if (user.hasPermission(builder + "*"))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static void explodeKnownWildcardPermissions(Permissible user)
    {
        PermissionAttachment attachment = null;
        for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins())
        {
            List<Permission> known_permissions = plugin.getDescription().getPermissions();

            for (Permission known_permission : known_permissions)
            {
                String name = known_permission.getName();
                String[] parts = name.split("\\.");
                StringBuilder builder = new StringBuilder();
                for (String part : parts)
                {
                    builder.append(part).append('.');
                    String current = builder.toString();
                    if (user.hasPermission(current + "*"))
                    {

                        for (Entry<String, Boolean> stringBooleanEntry : known_permission.getChildren().entrySet())
                        {
                            Entry<String, Boolean> entry = stringBooleanEntry;
                            if (!user.hasPermission(entry.getKey()))
                            {
                                if (attachment == null)
                                {
                                    attachment = user.addAttachment(plugin);
                                }

                                attachment.setPermission(entry.getKey(), true);
                            }
                        }
                    }
                }
            }

            attachment = null;
        }

    }
}
