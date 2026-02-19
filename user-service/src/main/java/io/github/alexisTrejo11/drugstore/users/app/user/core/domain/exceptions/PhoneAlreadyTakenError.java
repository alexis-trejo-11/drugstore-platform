package io.github.alexisTrejo11.drugstore.users.app.user.core.domain.exceptions;

import libs_kernel.exceptions.ConflictException;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.PhoneNumber;

public class PhoneAlreadyTakenError extends ConflictException {
  public PhoneAlreadyTakenError(PhoneNumber value) {
    super("Phone Already Taken: " + (value != null ? value.value() : "null"), "PHONE_ALREADY_TAKEN");
  }

}
