package microservice.auth.app.auth.core.domain.exceptions;

public class UserServiceAuthException extends RuntimeException {
  public UserServiceAuthException(String message) {
    super(message);
  }

}
