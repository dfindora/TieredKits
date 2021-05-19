package com.shortcircuit.utils.bukkit.command.exceptions;

public class NoPermissionException extends CommandException
{
    private final String requiredPermissions;

    public NoPermissionException()
    {
        this(null);
    }

    public NoPermissionException(String requiredPermissions)
    {
        super("I'm sorry, but you do not have permission to perform this command. "
              + "Please contact the server administrators if you believe this is in error.");
        this.requiredPermissions = requiredPermissions;
    }

    public String getRequiredPermissions()
    {
        return this.requiredPermissions;
    }
}
