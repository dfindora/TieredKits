package com.shortcircuit.utils.bukkit.command.exceptions;

public class BlockOnlyException extends CommandException {
    public BlockOnlyException() {
        super("This command is block-only");
    }
}
