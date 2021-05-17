package com.shortcircuit.utils.bukkit.command.exceptions;

public class LocatableOnlyException extends CommandException {
    public LocatableOnlyException() {
        super("This command is ingame-only");
    }
}
