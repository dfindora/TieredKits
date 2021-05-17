package com.shortcircuit.utils.bukkit.command;

import java.util.Set;

import org.bukkit.Server;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

class CommandSenderWrapper<T extends CommandSender> implements CommandSender
{
    protected final T wrap;

    protected CommandSenderWrapper(T wrap)
    {
        assert wrap instanceof BlockCommandSender || wrap instanceof Player;

        this.wrap = wrap;
    }

    public T getWrap()
    {
        return this.wrap;
    }

    public void sendMessage(String message)
    {
        this.wrap.sendMessage(message);
    }

    public void sendMessage(String[] messages)
    {
        this.wrap.sendMessage(messages);
    }

    public Server getServer()
    {
        return this.wrap.getServer();
    }

    public String getName()
    {
        return this.wrap.getName();
    }

    @Override
    public Spigot spigot()
    {
        return null;
    }

    public boolean isPermissionSet(String name)
    {
        return this.wrap.isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm)
    {
        return this.wrap.isPermissionSet(perm);
    }

    public boolean hasPermission(String name)
    {
        return PermissionUtils.hasPermission(this.wrap, name);
    }

    public boolean hasPermission(Permission perm)
    {
        return this.hasPermission(perm.getName());
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value)
    {
        return this.wrap.addAttachment(plugin, name, value);
    }

    public PermissionAttachment addAttachment(Plugin plugin)
    {
        return this.wrap.addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks)
    {
        return this.wrap.addAttachment(plugin, name, value);
    }

    public PermissionAttachment addAttachment(Plugin plugin, int ticks)
    {
        return this.wrap.addAttachment(plugin, ticks);
    }

    public void removeAttachment(PermissionAttachment attachment)
    {
        this.wrap.removeAttachment(attachment);
    }

    public void recalculatePermissions()
    {
        this.wrap.recalculatePermissions();
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions()
    {
        return this.wrap.getEffectivePermissions();
    }

    public boolean isOp()
    {
        return this.wrap.isOp();
    }

    public void setOp(boolean value)
    {
        this.wrap.setOp(value);
    }

    public boolean equals(Object other)
    {
        return other instanceof CommandSenderWrapper && this.wrap.equals(((CommandSenderWrapper<T>) other).wrap);
    }
}
