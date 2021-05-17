package com.shortcircuit.utils.bukkit.command.exceptions;

public class NoPermissionException extends CommandException {
    private final String required_permissions;

    public NoPermissionException() {
        this((String)null);
    }

    public NoPermissionException(String required_permissions) {
        super("I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe this is in error.");
        this.required_permissions = required_permissions;
    }

    public String getRequiredPermissions() {
        return this.required_permissions;
    }
}
