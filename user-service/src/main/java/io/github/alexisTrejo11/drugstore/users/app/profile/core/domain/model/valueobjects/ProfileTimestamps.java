package io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.valueobjects;

import java.time.LocalDate;

public record ProfileTimestamps(
    LocalDate joinedAt,
    LocalDate lastProfileUpdateAt) {

  public static final ProfileTimestamps NONE = new ProfileTimestamps(null, null);

  public ProfileTimestamps {
    if (joinedAt != null && joinedAt.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Joined date cannot be in the future");
    }
    if (lastProfileUpdateAt != null && lastProfileUpdateAt.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Last update date cannot be in the future");
    }
    if (joinedAt != null && lastProfileUpdateAt != null && lastProfileUpdateAt.isBefore(joinedAt)) {
      throw new IllegalArgumentException("Last update cannot be before joined date");
    }
  }

  public static ProfileTimestamps createNew() {
    LocalDate now = LocalDate.now();
    return new ProfileTimestamps(now, now);
  }

  public ProfileTimestamps updateLastModified() {
    return new ProfileTimestamps(joinedAt, LocalDate.now());
  }

  public boolean hasBeenUpdated() {
    return lastProfileUpdateAt != null && !lastProfileUpdateAt.equals(joinedAt);
  }
}