package com.shortcircuit.utils.bukkit.command.exceptions;

public class ConsoleOnlyException extends CommandException
{
    public ConsoleOnlyException()
    {
        super("This command is console-only.");
    }
}
