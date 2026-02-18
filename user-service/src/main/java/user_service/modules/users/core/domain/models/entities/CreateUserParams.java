package user_service.modules.users.core.domain.models.entities;

import lombok.Builder;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.valueobjects.Email;
import user_service.modules.users.core.domain.models.valueobjects.FullName;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;

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