package microservice.auth_service.app.auth.core.domain.exceptions;

public class UserServiceUnavailableException extends RuntimeException {
  public UserServiceUnavailableException(String message) {
    super(message);
  }

}
