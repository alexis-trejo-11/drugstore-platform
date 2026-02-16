package microservice.auth.app.auth.core.domain.exceptions;

import libs_kernel.exceptions.DomainException;

public class UserServiceTimeoutException extends DomainException {
  public UserServiceTimeoutException(String message) {
    super(message, null, "USER-SERVICE-TIMEOUT");
  }

}
