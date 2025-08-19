package microservice.users.core.application.dto;


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

    public static CommandResult failure(String message) {
        return new CommandResult(false, message, null);
    }
}
