package com.shortcircuit.utils.bukkit.command.exceptions;

public class InvalidArgumentException extends CommandException {
    public InvalidArgumentException() {
        this("Invalid argument");
    }

    public InvalidArgumentException(String message) {
        super(message);
    }

    public InvalidArgumentException(Throwable thrown) {
        super(thrown);
    }
}
