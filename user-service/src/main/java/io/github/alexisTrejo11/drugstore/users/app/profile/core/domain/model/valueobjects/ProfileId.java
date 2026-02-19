package io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.valueobjects;

import java.util.UUID;

public record ProfileId(String value) {

  public ProfileId {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("ProfileId cannot be null or empty");
    }
  }

  public static ProfileId of(UUID value) {
    return new ProfileId(value.toString());
  }

  public static ProfileId of(String value) {
    return new ProfileId(value);
  }

  public static ProfileId generate() {
    return new ProfileId(UUID.randomUUID().toString());
  }

  public UUID toUUID() {
    return UUID.fromString(value);
  }
}