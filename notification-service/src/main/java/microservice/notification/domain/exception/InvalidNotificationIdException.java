package microservice.notification.domain.exception;

/**
 * Exception thrown when a notification ID is invalid
 */
public class InvalidNotificationIdException extends NotificationDomainException {

  public InvalidNotificationIdException(String message) {
    super(message);
  }
}
