package microservice.auth.app;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import microservice.auth.app.auth.core.domain.valueobjects.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
public class User {
  private UserId id;
  private Email email;
  private String firstName;
  private String lastName;
  private PhoneNumber phoneNumber;
  private String password;
  private UserRole role;
  private LocalDateTime joinedAt;
  private LocalDateTime lastLoginAt;

  protected User(UserId id, Email email, PhoneNumber phoneNumber, String password, UserRole role) {
    this.id = Objects.requireNonNull(id);
    this.email = Objects.requireNonNull(email);
    this.password = Objects.requireNonNull(password);
    this.role = Objects.requireNonNull(role);
    this.joinedAt = LocalDateTime.now();
    this.phoneNumber = Objects.requireNonNull(phoneNumber);
    this.lastLoginAt = LocalDateTime.now();
  }

  public static User create(Email email, PhoneNumber phoneNumber, String password, UserRole role) {
    return new User(UserId.generate(), email, phoneNumber, password, role);
  }

  public void updateEmail(Email newEmail) {
    this.email = newEmail;
  }

  public void changePassword(String newPassword) {
    this.password = newPassword;
  }
}
