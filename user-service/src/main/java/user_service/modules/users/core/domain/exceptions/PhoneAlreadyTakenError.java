package user_service.modules.users.core.domain.exceptions;

import libs_kernel.exceptions.ConflictException;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;

public class PhoneAlreadyTakenError extends ConflictException {
  public PhoneAlreadyTakenError(PhoneNumber value) {
    super("Phone Already Taken: " + (value != null ? value.value() : "null"), "PHONE_ALREADY_TAKEN");
  }

}
