package com.shortcircuit.utils.bukkit.command.exceptions;

public class PlayerOnlyException extends CommandException {
    public PlayerOnlyException() {
        super("This command is player-only");
    }
}
