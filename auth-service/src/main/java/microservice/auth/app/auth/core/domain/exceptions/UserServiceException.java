package microservice.auth.app.auth.core.domain.exceptions;

public class UserServiceException extends RuntimeException {
  public UserServiceException(String message, Throwable cause) {
    super(message, cause);
  }

}
