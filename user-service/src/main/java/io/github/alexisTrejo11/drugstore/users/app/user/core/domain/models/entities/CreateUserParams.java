package io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.entities;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.FullName;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.PhoneNumber;

@Builder
public record CreateUserParams(
    Email email,
    FullName fullName,
    PhoneNumber phoneNumber,
    String hashedPassword,
    UserRole role,
    Boolean twoFactorEnabled) {

  // Convenience constructor with twoFactorEnabled defaulting to false
  public CreateUserParams(Email email, FullName fullName, PhoneNumber phoneNumber,
      String hashedPassword, UserRole role) {
    this(email, fullName, phoneNumber, hashedPassword, role, false);
  }
}