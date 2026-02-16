package microservice.auth.app.auth.core.domain.exceptions;

import libs_kernel.exceptions.NotFoundException;
import microservice.auth.app.auth.core.domain.valueobjects.UserId;

public class UserNotFoundException extends NotFoundException {
  public UserNotFoundException(UserId userId) {
    super("User", "ID", userId.value());
  }

  public UserNotFoundException(String message) {
    super(message);
  }

}
