package microservice.notification.domain.exception;

/**
 * Base exception for all domain-related exceptions in the notification service
 */
public class NotificationDomainException extends RuntimeException {

  public NotificationDomainException(String message) {
    super(message);
  }

  public NotificationDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
