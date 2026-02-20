package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder
public record EmailVerificationEvent(
    String eventId,
    String eventType,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime eventTimestamp,
    String correlationId,
    String userId,
    String email,
    String firstName,
    String lastName,
    String verificationToken,
    String verificationCode,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime expirestAt,
    String language,
    String callbackUrl,
    Boolean isResend,
    Integer attemptCount) {

}
