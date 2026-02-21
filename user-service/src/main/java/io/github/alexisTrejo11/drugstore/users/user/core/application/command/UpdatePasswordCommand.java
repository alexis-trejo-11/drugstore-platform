package io.github.alexisTrejo11.drugstore.users.user.core.application.command;

import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

public record UpdatePasswordCommand(
        UserId userId,
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
