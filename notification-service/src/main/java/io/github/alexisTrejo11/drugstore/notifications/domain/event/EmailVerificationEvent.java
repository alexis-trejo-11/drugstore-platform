package io.github.alexisTrejo11.drugstore.notifications.domain.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record EmailVerificationEvent(
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
    // Verification data
    String verificationToken,
    String verificationCode,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime expiresAt,
    // Configuration
    String language,
    String callbackUrl,
    // Metadata
    Boolean isResend,
    Integer attemptNumber) {
}