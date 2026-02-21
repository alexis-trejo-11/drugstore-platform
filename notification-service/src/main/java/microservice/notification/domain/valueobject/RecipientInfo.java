package microservice.notification.domain.valueobject;

import microservice.notification.domain.exception.InvalidRecipientException;

import java.io.Serializable;

/**
 * Value Object containing recipient information
 */
public record RecipientInfo(
    String userId,
    String email,
    String phoneNumber,
    String firstName,
    String lastName,
    String language,
    String timezone) implements Serializable {

  public String getFullName() {
    if (firstName == null && lastName == null) {
      return null;
    }
    return String.format("%s %s",
        firstName != null ? firstName : "",
        lastName != null ? lastName : "").trim();
  }

  public String getLanguageOrDefault() {
    return language != null ? language : "en";
  }

  public void validate(NotificationChannel channel) {
    if (userId == null || userId.trim().isEmpty()) {
      throw new InvalidRecipientException("Recipient userId is required");
    }

    switch (channel) {
      case EMAIL:
        if (email == null || email.trim().isEmpty()) {
          throw new InvalidRecipientException("Recipient email is required for EMAIL channel");
        }
        break;
      case SMS:
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
          throw new InvalidRecipientException("Recipient phone number is required for SMS channel");
        }
        break;
    }
  }
}
