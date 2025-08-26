package user_service.modules.users.core.application.command;

import lombok.Builder;
import java.util.UUID;

@Builder
public record UpdatePasswordCommand(
        UUID userId,
        String newPlainPassword) implements Command {
    public UpdatePasswordCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (newPlainPassword == null || newPlainPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be null or blank");
        }
    }
}
