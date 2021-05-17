package com.shortcircuit.utils.bukkit.command;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public enum CommandType {
    CONSOLE("console", ConsoleCommandSender.class),
    PLAYER("player", Player.class),
    BLOCK("block", BlockCommandSender.class),
    LOCATABLE("ingame", Player.class, BlockCommandSender.class, LocatableCommandSender.class),
    ANY("all", ConsoleCommandSender.class, Player.class, BlockCommandSender.class, LocatableCommandSender.class);

    private final String applicable_sender_name;
    private final Class<? extends CommandSender>[] applicable_senders;

    @SafeVarargs
    private CommandType(String applicable_sender_name, Class<? extends CommandSender>... applicable_senders) {
        this.applicable_sender_name = applicable_sender_name;
        this.applicable_senders = applicable_senders;
    }

    public String getApplicableSenderName() {
        return this.applicable_sender_name;
    }

    public Class<? extends CommandSender>[] getApplicableSenders() {
        return this.applicable_senders.clone();
    }

    public boolean isSenderApplicable(CommandSender sender) {
        Class<? extends CommandSender> check = sender.getClass();
        for (Class<? extends CommandSender> applicable_sender : this.applicable_senders)
        {
            if (applicable_sender.isAssignableFrom(check))
            {
                return true;
            }
        }
        return false;
    }
}
