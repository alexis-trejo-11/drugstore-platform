package user_service.modules.users.core.application.command;

public record UpdateUserPasswordCommand(
        String userId,
        String oldPassword,
        String newPassword) implements Command {
    public UpdateUserPasswordCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new IllegalArgumentException("Old password cannot be null or blank");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be null or blank");
        }
    }
}
