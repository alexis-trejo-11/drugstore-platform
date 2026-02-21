package io.github.alexisTrejo11.drugstore.users.user.core.application.command;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.FullName;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;

@Builder
public record CreateUserCommand(
    Email email,
    PhoneNumber phoneNumber,
    UserRole role,
    FullName fullName,
    String password,
    boolean isPasswordHashed) implements Command {
}
