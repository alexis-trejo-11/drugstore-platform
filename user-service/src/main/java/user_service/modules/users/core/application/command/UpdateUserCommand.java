package user_service.modules.users.core.application.command;

import user_service.modules.users.core.domain.models.valueobjects.Email;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;

import user_service.modules.users.core.domain.models.valueobjects.UserId;

public record UpdateUserCommand(
        UserId userId,
        Email email,
        PhoneNumber phoneNumber) implements Command {
    public UpdateUserCommand {
        if (userId == null) {
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