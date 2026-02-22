package io.github.alexisTrejo11.drugstore.notifications.domain.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record TwoFactorCodeEvent(
    // Event metadata
    String eventId,
    String eventType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime eventTimestamp,
    String correlationId,
    // Recipient information
    String userId,
    String email,
    String phoneNumber,
    String firstName,
    // 2FA code
    String code,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime expiresAt,
    // Channel configuration (EMAIL, SMS, BOTH)
    String channel,
    // Context ("login", "enable-2fa", "sensitive-operation")
    String purpose,
    String ipAddress,
    String deviceName,
    String language) {
}