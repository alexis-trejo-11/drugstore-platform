package io.github.alexisTrejo11.drugstore.notifications.domain.valueobject;

import io.github.alexisTrejo11.drugstore.notifications.domain.exception.InvalidNotificationIdException;

import java.io.Serializable;
import java.util.UUID;

/**
 * Value Object representing a unique notification identifier
 */
public record NotificationId(String value) implements Serializable {

  public NotificationId {
    if (value == null || value.trim().isEmpty()) {
      throw new InvalidNotificationIdException("NotificationId cannot be null or empty");
    }
  }

  public static NotificationId generate() {
    return new NotificationId(UUID.randomUUID().toString());
  }

  public static NotificationId from(String value) {
    return new NotificationId(value);
  }
}
