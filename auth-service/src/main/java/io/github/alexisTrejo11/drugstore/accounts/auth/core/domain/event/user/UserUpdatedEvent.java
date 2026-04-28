package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.user;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder
public record UserUpdatedEvent(
    String eventId,
    String eventType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime eventTimestamp,
    String correlationId,
    String userId,
    Map<String, Object> updatedFields,
    Map<String, Object> previousValues,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt,
    String updatedBy,
    String source) {
}
