package io.github.alexisTrejo11.drugstore.notifications.domain.exception;

/**
 * Exception thrown when a notification type is invalid
 */
public class InvalidNotificationTypeException extends NotificationDomainException {

  public InvalidNotificationTypeException(String message) {
    super(message);
  }
}
