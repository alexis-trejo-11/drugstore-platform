package io.github.alexisTrejo11.drugstore.notifications.domain.valueobject;

import java.io.Serializable;

import io.github.alexisTrejo11.drugstore.notifications.domain.exception.InvalidRecipientException;

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

  public static final RecipientInfo NONE = new RecipientInfo(
      null, null, null, null, null, null, null);

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
      case EMAIL -> {
        if (email == null || email.trim().isEmpty()) {
          throw new InvalidRecipientException("Recipient email is required for EMAIL channel");
        }
      }
      case SMS -> {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
          throw new InvalidRecipientException("Recipient phone number is required for SMS channel");
        }
      }
      default -> throw new InvalidRecipientException("Unexpected value: " + channel);
    }
  }

  // Builder pattern
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String userId;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String language;
    private String timezone;

    private Builder() {
    }

    public Builder userId(String userId) {
      this.userId = userId;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder phoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder language(String language) {
      this.language = language;
      return this;
    }

    public Builder timezone(String timezone) {
      this.timezone = timezone;
      return this;
    }

    public RecipientInfo build() {
      return new RecipientInfo(userId, email, phoneNumber, firstName, lastName, language, timezone);
    }
  }
}
