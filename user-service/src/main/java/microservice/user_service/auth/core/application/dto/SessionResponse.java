package microservice.user_service.auth.core.application.dto;

import lombok.Builder;
import microservice.user_service.auth.core.domain.session.Session;

import java.time.format.DateTimeFormatter;

@Builder
public record SessionResponse(
        String accessToken,
        String refreshToken,
        String userId,
        String tokenType,
        String expiresAt
) {
    public static SessionResponse from(Session session) {
        return SessionResponse.builder()
                .accessToken(session.getAccessToken())
                .refreshToken(session.getRefreshToken())
                .userId(session.getUserId().toString())
                .tokenType(session.getType())
                .expiresAt(session.getExpiresAt().format(DateTimeFormatter.BASIC_ISO_DATE))
                .build();
    }
}
