package microservice.notification.domain.exception;

/**
 * Exception thrown when a notification channel is invalid
 */
public class InvalidNotificationChannelException extends NotificationDomainException {

  public InvalidNotificationChannelException(String message) {
    super(message);
  }
}
