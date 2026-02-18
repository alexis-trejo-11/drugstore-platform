package user_service.modules.profile.core.domain.model.valueobjects;

import java.time.LocalDate;

import user_service.modules.users.core.domain.models.enums.Gender;
import user_service.modules.users.core.domain.models.valueobjects.FullName;

public record PersonalData(
    FullName fullName,
    LocalDate dateOfBirth,
    Gender gender) {

  public static final PersonalData NONE = new PersonalData(null, null, null);

  public PersonalData {
    // Allow nullable fields but validate if provided
    if (fullName != null) {
      if (fullName.firstName() != null && fullName.firstName().trim().isEmpty()) {
        throw new IllegalArgumentException("First name cannot be empty");
      }
      if (fullName.lastName() != null && fullName.lastName().trim().isEmpty()) {
        throw new IllegalArgumentException("Last name cannot be empty");
      }
    }
    if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date of birth cannot be in the future");
    }
  }

  public boolean isComplete() {
    return fullName != null && fullName.firstName() != null && fullName.lastName() != null && dateOfBirth != null
        && gender != null;
  }

  public PersonalData update(FullName fullName, LocalDate newDate, Gender gender) {
    return new PersonalData(
        fullName != null ? fullName : this.fullName,
        newDate != null ? newDate : this.dateOfBirth,
        gender != null ? gender : this.gender);
  }
}