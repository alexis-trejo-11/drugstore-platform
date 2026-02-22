package io.github.alexisTrejo11.drugstore.notifications.domain.valueobject;

import io.github.alexisTrejo11.drugstore.notifications.domain.exception.InvalidNotificationTypeException;

/**
 * Enum representing the type/purpose of the notification
 */
public enum NotificationType {
  // Authentication related
  EMAIL_VERIFICATION("email_verification", "Account Verification"),
  TWO_FACTOR_CODE("two_factor_code", "Two-Factor Authentication"),
  PASSWORD_RESET("password_reset", "Password Reset"),
  PASSWORD_CHANGED("password_changed", "Password Changed Confirmation"),

  // Welcome and onboarding
  WELCOME("welcome", "Welcome Message"),
  ACCOUNT_ACTIVATED("account_activated", "Account Activated"),

  // Informational
  LOGIN_ALERT("login_alert", "Login Alert"),
  SECURITY_ALERT("security_alert", "Security Alert"),
  PROFILE_UPDATED("profile_updated", "Profile Updated"),

  // Marketing
  PROMOTIONAL("promotional", "Promotional Message"),
  NEWSLETTER("newsletter", "Newsletter"),
  PRODUCT_UPDATE("product_update", "Product Update");

  private final String code;
  private final String displayName;

  NotificationType(String code, String displayName) {
    this.code = code;
    this.displayName = displayName;
  }

  public String getCode() {
    return code;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static NotificationType fromCode(String code) {
    for (NotificationType type : NotificationType.values()) {
      if (type.code.equalsIgnoreCase(code)) {
        return type;
      }
    }
    throw new InvalidNotificationTypeException("Invalid notification type: " + code);
  }
}
