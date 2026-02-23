package io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing employee contact information
 */
public class ContactInfo implements Serializable {

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

  private final String email;
  private final String phoneNumber;
  private final String emergencyContact;
  private final String emergencyPhone;

  private ContactInfo(String email, String phoneNumber, String emergencyContact, String emergencyPhone) {
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.emergencyContact = emergencyContact;
    this.emergencyPhone = emergencyPhone;
  }

  public static ContactInfo of(String email, String phoneNumber, String emergencyContact, String emergencyPhone) {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be null or empty");
    }
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new IllegalArgumentException("Invalid email format");
    }
    if (phoneNumber != null && !phoneNumber.trim().isEmpty() && !PHONE_PATTERN.matcher(phoneNumber).matches()) {
      throw new IllegalArgumentException("Invalid phone number format");
    }
    if (emergencyPhone != null && !emergencyPhone.trim().isEmpty()
        && !PHONE_PATTERN.matcher(emergencyPhone).matches()) {
      throw new IllegalArgumentException("Invalid emergency phone number format");
    }
    return new ContactInfo(email, phoneNumber, emergencyContact, emergencyPhone);
  }

  public String getEmail() {
    return email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getEmergencyContact() {
    return emergencyContact;
  }

  public String getEmergencyPhone() {
    return emergencyPhone;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ContactInfo that = (ContactInfo) o;
    return Objects.equals(email, that.email) &&
        Objects.equals(phoneNumber, that.phoneNumber) &&
        Objects.equals(emergencyContact, that.emergencyContact) &&
        Objects.equals(emergencyPhone, that.emergencyPhone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, phoneNumber, emergencyContact, emergencyPhone);
  }

  @Override
  public String toString() {
    return "ContactInfo{" +
        "email='" + email + '\'' +
        ", phoneNumber='" + phoneNumber + '\'' +
        '}';
  }
}
