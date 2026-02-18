package user_service.modules.profile.core.domain.model.valueobjects;

import java.time.LocalDate;
import user_service.modules.users.core.domain.models.enums.Gender;

public record PersonalData(
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    Gender gender) {

  public static final PersonalData NONE = new PersonalData(null, null, null, null);

  public PersonalData {
    // Allow nullable fields but validate if provided
    if (firstName != null && firstName.trim().isEmpty()) {
      throw new IllegalArgumentException("First name cannot be empty");
    }
    if (lastName != null && lastName.trim().isEmpty()) {
      throw new IllegalArgumentException("Last name cannot be empty");
    }
    if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date of birth cannot be in the future");
    }
  }

  public String getFullName() {
    if (firstName == null && lastName == null) {
      return null;
    }
    String first = firstName != null ? firstName : "";
    String last = lastName != null ? lastName : "";
    return (first + " " + last).trim();
  }

  public boolean isComplete() {
    return firstName != null && lastName != null && dateOfBirth != null && gender != null;
  }
}