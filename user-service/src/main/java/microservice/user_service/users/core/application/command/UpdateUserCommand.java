package microservice.user_service.users.core.application.command;

import microservice.user_service.users.core.domain.models.valueobjects.Email;
import microservice.user_service.users.core.domain.models.valueobjects.PhoneNumber;

import java.util.UUID;

public record UpdateUserCommand(
        UUID userId,
        Email email,
        PhoneNumber phoneNumber
) implements Command {
    public UpdateUserCommand {
        if (userId == null ) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        if (phoneNumber == null) {
            throw new IllegalArgumentException("Phone number cannot be null");
        }
    }

}