package io.github.alexisTrejo11.drugstore.users.user.core.domain.exceptions;

import libs_kernel.exceptions.ConflictException;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Email;

public class EmailAlreadyTakenError extends ConflictException {
  public EmailAlreadyTakenError(Email email) {
    super("Email already taken: " + (email != null ? email.value() : "null"), "EMAIL_ALREADY_TAKEN");
  }
}
