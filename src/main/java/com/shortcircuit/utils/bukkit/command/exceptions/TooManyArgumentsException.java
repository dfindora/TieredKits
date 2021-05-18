package com.shortcircuit.utils.bukkit.command.exceptions;

public class TooManyArgumentsException extends CommandException
{
    public TooManyArgumentsException()
    {
        super("Too many arguments");
    }

    public TooManyArgumentsException(String message)
    {
        super(message);
    }
}
