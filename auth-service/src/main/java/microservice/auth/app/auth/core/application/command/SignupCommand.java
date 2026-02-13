package microservice.auth.app.auth.core.application.command;

import java.time.LocalDate;

import microservice.auth.app.auth.core.domain.valueobjects.Email;
import microservice.auth.app.auth.core.domain.valueobjects.Password;
import microservice.auth.app.auth.core.domain.valueobjects.PhoneNumber;
import microservice.auth.app.auth.core.domain.valueobjects.UserRole;

public record SignupCommand(
    Email email,
    PhoneNumber phone,
    Password password,
    UserRole role) {
  public SignupCommand {
    if (role == null) {
      throw new IllegalArgumentException("Role cannot be null");
    }
    if (email == null || email.value().trim().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be empty");
    }
    if (phone == null || phone.value().trim().isEmpty()) {
      throw new IllegalArgumentException("Phone cannot be empty");
    }
    if (password == null || password.value().length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters");
    }
  }

  public static SignupCommand of(String email, String phone, String password, UserRole role) {

    return new SignupCommand(
        new Email(email),
        new PhoneNumber(phone),
        new Password(password),
        role);
  }

  public record PersonalInfo(String firstName, String lastName, LocalDate dateOfBirth, String gender) {
    public PersonalInfo {
      if (firstName == null || firstName.trim().isEmpty()) {
        throw new IllegalArgumentException("First name cannot be empty");
      }
      if (lastName == null || lastName.trim().isEmpty()) {
        throw new IllegalArgumentException("Last name cannot be empty");
      }
      if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("Date of birth must be in the past");
      }
    }
  }
}
