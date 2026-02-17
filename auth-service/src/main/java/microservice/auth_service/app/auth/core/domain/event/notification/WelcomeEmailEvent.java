package microservice.auth_service.app.auth.core.domain.event.notification;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder
public record WelcomeEmailEvent(
    String eventId,
    String eventType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime eventTimestamp,
    String correlationId,
    String userId,
    String email,
    String firstName,
    String lastName,
    String language,
    String accountType,
    String dashboardUrl,
    String profileUrl,
    String supportUrl,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime registeredAt,
    String registrationSource) {
}
