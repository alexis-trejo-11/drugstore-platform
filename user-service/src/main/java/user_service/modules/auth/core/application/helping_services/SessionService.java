package user_service.modules.auth.core.application.helping_services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import user_service.modules.auth.core.application.dto.LoginMetadata;
import user_service.modules.auth.core.domain.exception.UserSessionNotFound;
import user_service.modules.auth.core.domain.repositories.SessionRepository;
import user_service.modules.auth.core.domain.session.Session;
import user_service.modules.auth.core.domain.session.UserClaims;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.utils.tokens.interfaces.TokenType;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    private final SessionRepository sessionRepository;
    private final TokenService authTokenService;

    public void deleteAllUserSession(UUID userId) {
        sessionRepository.deactivateAllUserSessions(userId);
    }

    public void deleteUserSession(String sessionId, UUID userId) {
        Session session = sessionRepository.findUserSession(sessionId, userId)
                .orElseThrow(() -> new UserSessionNotFound(userId));

        sessionRepository.deactivateUserSession(userId, session.getRefreshToken());
    }

    public Session refreshUserSession(String sessionId, User user) {
        Session session = sessionRepository.findUserSession(sessionId, user.getId())
                .orElseThrow(() -> new UserSessionNotFound(user.getId()));

        var accessToken = authTokenService.createToken(
                UserClaims.from(user),
                user.getId().toString(),
                TokenType.ACCESS);

        session.setAccessToken(accessToken.generate());
        return session;
    }

    public Session createUserSession(User user, LoginMetadata loginMetadata) {
        try {
            UserClaims userClaims = UserClaims.from(user);

            var refreshToken = authTokenService.createToken(userClaims, user.getId().toString(), TokenType.REFRESH);

            var accessToken = authTokenService.createToken(userClaims, user.getId().toString(), TokenType.ACCESS);

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
