package user_service.modules.users.core.domain.models.valueobjects;

import java.util.UUID;

public record UserId(String value) {
  public UserId {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("UserId cannot be null or empty");
    }
  }

  public static UserId of(UUID value) {
    return new UserId(value.toString());
  }

  public static UserId of(String value) {
    return new UserId(value);
  }

  public static UserId generate() {
    return new UserId(UUID.randomUUID().toString());
  }

}
