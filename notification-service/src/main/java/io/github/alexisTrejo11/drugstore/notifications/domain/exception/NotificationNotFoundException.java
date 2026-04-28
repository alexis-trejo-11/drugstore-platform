package io.github.alexisTrejo11.drugstore.notifications.domain.exception;

/**
 * Exception thrown when a notification is not found
 */
public class NotificationNotFoundException extends NotificationDomainException {

  public NotificationNotFoundException(String message) {
    super(message);
  }

  public NotificationNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
