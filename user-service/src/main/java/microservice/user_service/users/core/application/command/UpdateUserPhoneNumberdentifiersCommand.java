package microservice.user_service.users.core.application.command;

public record UpdateUserPhoneNumberdentifiersCommand(
        String userId,
        String email,
        String phoneNumber
) implements Command {
    public UpdateUserPhoneNumberdentifiersCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
    }

}