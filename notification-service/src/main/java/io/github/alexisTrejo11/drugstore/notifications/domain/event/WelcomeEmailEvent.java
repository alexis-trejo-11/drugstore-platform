package io.github.alexisTrejo11.drugstore.notifications.domain.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record WelcomeEmailEvent(
    // Event metadata
    String eventId,
    String eventType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime eventTimestamp,
    String correlationId,
    // Recipient information
    String userId,
    String email,
    String firstName,
    String lastName,
    // Personalization
    String language,
    String accountType,
    // URLs
    String dashboardUrl,
    String profileUrl,
    String supportUrl,
    // Additional info
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime registeredAt,
    String registrationSource) {
}
