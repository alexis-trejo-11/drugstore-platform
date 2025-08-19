package microservice.users.core.application.command;

public record UpdateUserIdentifiersCommand(
        String userId,
        String email,
        String phoneNumber
) implements Command {
    public UpdateUserIdentifiersCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
    }

}