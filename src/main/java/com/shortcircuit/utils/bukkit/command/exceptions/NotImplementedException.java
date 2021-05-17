package com.shortcircuit.utils.bukkit.command.exceptions;

public class NotImplementedException extends CommandException {
    public NotImplementedException() {
        super("This command has not been implemented.");
    }
}
