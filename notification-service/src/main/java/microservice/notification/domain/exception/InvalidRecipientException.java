package microservice.notification.domain.exception;

/**
 * Exception thrown when recipient information is invalid
 */
public class InvalidRecipientException extends NotificationDomainException {

  public InvalidRecipientException(String message) {
    super(message);
  }
}
