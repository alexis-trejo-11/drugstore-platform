package microservice.user_service.auth.core.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.user_service.auth.core.application.dto.LoginMetadata;
import microservice.user_service.auth.core.domain.repositories.SessionRepository;
import microservice.user_service.auth.core.domain.session.Session;
import microservice.user_service.auth.core.ports.output.UserClaims;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.utils.tokens.interfaces.TokenType;

import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    private final SessionRepository sessionRepository;
    private final TokenService authTokenService;

    public void deleteAllUserSession(UUID userId) {
        sessionRepository.deactivateAllUserSessions(userId.toString());
    }

    public void deleteUserSession(String sessionId, UUID userId) {
        Session session = sessionRepository.findUserSession(sessionId, userId.toString());
        if (session == null) {
            throw new IllegalArgumentException("No active session found for the given user and token");
        }

        sessionRepository.deactivateUserSession(userId.toString(), sessionId);
    }

    public Session refreshUserSession(String sessionId, UUID userId) {
        Session session = sessionRepository.findUserSession(sessionId, userId.toString());
        if (session == null) {
            throw new IllegalArgumentException("No active session found for the given user and token");
        }

        var accessToken = authTokenService.createToken(
                UserClaims.accessClaims(userId.toString()),
                TokenType.ACCESS);

        session.setAccessToken(accessToken.generate());
        return session;
    }

    public Session createUserSession(User user, LoginMetadata loginMetadata) {
        try {
            UserClaims userClaims = new UserClaims(
                    user.getId().toString(),
                    user.getEmail(),
                    user.getRole().getRoleName());

            var refreshToken = authTokenService.createToken(userClaims, TokenType.REFRESH);

            var accessToken = authTokenService.createToken(userClaims, TokenType.ACCESS);

            String safeUserAgent = loginMetadata != null ? loginMetadata.userAgent() : "";
            String safeIpAddress = loginMetadata != null ? loginMetadata.ipAddress() : "";
            String safeDevice = loginMetadata != null ? loginMetadata.deviceType() : "";

            Session session = Session.createSafeSession(
                    user.getId(),
                    accessToken.generate(),
                    refreshToken.generate(),
                    safeUserAgent,
                    safeIpAddress,
                    safeDevice);

            Session savedSession = sessionRepository.save(session);

            return savedSession;

        } catch (Exception e) {
            log.error("Error creating user session for user: {}", user.getId(), e);
            throw new RuntimeException("Failed to create user session for user: " + user.getId(), e);
        }
    }
}
