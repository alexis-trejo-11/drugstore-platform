package microservice.user_service.auth.core.application;

import lombok.RequiredArgsConstructor;
import microservice.user_service.auth.core.application.dto.LoginMetadata;
import microservice.user_service.auth.core.domain.repositories.SessionRepository;
import microservice.user_service.auth.core.domain.session.Session;
import microservice.user_service.auth.core.ports.output.AuthTokenService;
import microservice.user_service.auth.core.ports.output.UserClaims;
import microservice.user_service.users.core.domain.models.entities.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final AuthTokenService authTokenService;

    public void deleteAllUserSession(UUID userId) {
        sessionRepository.deactivateAllUserSessions(userId.toString());
    }

    public void deleteUserSession(String sessionId, UUID userId) {
        sessionRepository.deactivateUserSession(userId.toString(), sessionId);
    }

    public Session refreshUserSession(String sessionId, UUID userId) {
        Session session = sessionRepository.findUserSession(sessionId, userId.toString());
        String accessToken = authTokenService.generateAccessToken(Duration.ofHours(1));
        session.setAccessToken(accessToken);
        return session;
    }

    public Session createUserSession(User user, LoginMetadata loginMetadata) {
        UserClaims userClaims = new UserClaims(
                user.getId().toString(),
                user.getEmail(),
                user.getRole().getRoleName());
        String refreshToken = authTokenService.generateRefreshToken(userClaims, Duration.ofDays(30));
        String accessToken = authTokenService.generateAccessToken(Duration.ofHours(1));

        Session session = Session.builder()
                .userId(user.getId())
                .active(true)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now())
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .ipAddress(loginMetadata.ipAddress())
                .userAgent(loginMetadata.userAgent())
                .device(loginMetadata.deviceType())
                .build();
        return sessionRepository.save(session);
    }
}
