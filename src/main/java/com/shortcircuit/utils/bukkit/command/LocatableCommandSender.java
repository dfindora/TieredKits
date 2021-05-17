package com.shortcircuit.utils.bukkit.command;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocatableCommandSender extends CommandSenderWrapper<CommandSender>
{
    protected LocatableCommandSender(CommandSender wrap)
    {
        super(wrap);

        assert wrap instanceof BlockCommandSender || wrap instanceof Player;

    }

    public Location getLocation()
    {
        return this.wrap instanceof BlockCommandSender ? ((BlockCommandSender) this.wrap).getBlock().getLocation()
                                                       : ((Player) this.wrap).getLocation();
    }

    public boolean isPlayer()
    {
        return this.wrap instanceof Player;
    }

    public boolean isBlock()
    {
        return this.wrap instanceof BlockCommandSender;
    }

    public Player asPlayer() throws ClassCastException
    {
        return (Player) this.wrap;
    }

    public BlockCommandSender asBlock() throws ClassCastException
    {
        return (BlockCommandSender) this.wrap;
    }

    public boolean equals(Object other)
    {
        return other instanceof LocatableCommandSender && this.wrap.equals(((CommandSenderWrapper<CommandSender>) other).wrap);
    }
}
