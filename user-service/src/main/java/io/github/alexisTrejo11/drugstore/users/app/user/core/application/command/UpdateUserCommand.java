package io.github.alexisTrejo11.drugstore.users.app.user.core.application.command;

import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.PhoneNumber;

import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;

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