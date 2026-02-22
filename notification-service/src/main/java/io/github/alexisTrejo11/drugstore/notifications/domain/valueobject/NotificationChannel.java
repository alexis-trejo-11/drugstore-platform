package io.github.alexisTrejo11.drugstore.notifications.domain.valueobject;

import io.github.alexisTrejo11.drugstore.notifications.domain.exception.InvalidNotificationChannelException;

/**
 * Enum representing the communication channel for notifications
 */
public enum NotificationChannel {
  EMAIL("email"),
  SMS("sms"),
  PUSH("push"),
  IN_APP("in_app");

  private final String value;

  NotificationChannel(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static NotificationChannel fromString(String value) {
    for (NotificationChannel channel : NotificationChannel.values()) {
      if (channel.value.equalsIgnoreCase(value)) {
        return channel;
      }
    }
    throw new InvalidNotificationChannelException("Invalid notification channel: " + value);
  }
}
