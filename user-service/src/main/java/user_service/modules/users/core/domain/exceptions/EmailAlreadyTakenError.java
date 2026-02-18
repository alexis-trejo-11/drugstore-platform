package user_service.modules.users.core.domain.exceptions;

import libs_kernel.exceptions.ConflictException;
import user_service.modules.users.core.domain.models.valueobjects.Email;

public class EmailAlreadyTakenError extends ConflictException {
  public EmailAlreadyTakenError(Email email) {
    super("Email already taken: " + (email != null ? email.value() : "null"), "EMAIL_ALREADY_TAKEN");
  }
}
