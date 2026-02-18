package user_service.modules.users.core.domain.models.valueobjects;

public record FullName(String firstName, String lastName) {
  public static FullName NONE = new FullName("N/A", "N/A");

  public FullName {
    if (firstName == null || firstName.isEmpty()) {
      throw new IllegalArgumentException("First name cannot be null or empty");
    }
    if (lastName == null || lastName.isEmpty()) {
      throw new IllegalArgumentException("Last name cannot be null or empty");
    }

    if (!firstName.matches("^[A-Za-z]+$")) {
      throw new IllegalArgumentException("First name can only contain letters");
    }
    if (!lastName.matches("^[A-Za-z]+$")) {
      throw new IllegalArgumentException("Last name can only contain letters");
    }

    if (firstName.length() < 3 || firstName.length() > 100) {
      throw new IllegalArgumentException("First name must be between 3 and 100 characters");
    }
    if (lastName.length() < 3 || lastName.length() > 100) {
      throw new IllegalArgumentException("Last name must be between 3 and 100 characters");
    }
  }
}
