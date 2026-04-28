package io.github.alexisTrejo11.drugstore.notifications.domain.exception;

/**
 * Exception thrown when a notification ID is invalid
 */
public class InvalidNotificationIdException extends NotificationDomainException {

  public InvalidNotificationIdException(String message) {
    super(message);
  }
}
