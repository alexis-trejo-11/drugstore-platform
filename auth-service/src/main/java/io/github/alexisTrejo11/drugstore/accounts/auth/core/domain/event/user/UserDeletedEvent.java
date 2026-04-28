package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.user;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder
public record UserDeletedEvent(
    String eventId,
    String eventType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime eventTimestamp,
    String correlationId,
    String userId,
    String email,
    DeletionType deletionType,
    String reason,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime deletedAt,
    String deletedBy,
    String source) {
  public enum DeletionType {
    SOFT_DELETE,
    HARD_DELETE,
    DEACTIVATION
  }
}
