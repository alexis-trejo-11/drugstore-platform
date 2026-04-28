package io.github.alexisTrejo11.drugstore.users.user.core.application.result;

public record CommandResult(boolean success, String message, Object data) {
    public CommandResult {
        if (message == null) {
            message = "";
        }
        if (data == null) {
            data = new Object();
        }
    }

    public static CommandResult success(String message, Object data) {
        return new CommandResult(true, message, data);
    }

    public static CommandResult success(String message) {
        return new CommandResult(true, message, null);
    }

    public static CommandResult failure(String message) {
        return new CommandResult(false, message, null);
    }
}
