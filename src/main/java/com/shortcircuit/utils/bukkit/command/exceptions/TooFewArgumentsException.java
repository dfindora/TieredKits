package com.shortcircuit.utils.bukkit.command.exceptions;

public class TooFewArgumentsException extends CommandException
{
    public TooFewArgumentsException()
    {
        super("Too few arguments.");
    }

    public TooFewArgumentsException(String message)
    {
        super("Too few arguments. Usage: " + message);
    }
}
