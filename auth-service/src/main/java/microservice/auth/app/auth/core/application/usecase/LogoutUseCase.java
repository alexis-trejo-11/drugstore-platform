package microservice.auth.app.auth.core.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.auth.app.auth.core.application.command.LogoutCommand;
import microservice.auth.app.auth.core.domain.exceptions.InvalidCredentialsException;
import microservice.auth.app.auth.core.ports.input.TokenProvider;
import microservice.auth.app.auth.core.ports.output.SessionService;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * LogoutUseCase - Handles user logout by invalidating the refresh token
 * This DDD ApplicationService blacklists the refresh token to prevent reuse
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutUseCase {
    private final SessionService sessionService;
    private final TokenProvider tokenProvider;

    // Duration for which the token should be blacklisted (typical refresh token lifetime)
    private static final Duration BLACKLIST_DURATION = Duration.ofDays(7);

    /**
     * Execute the logout use case
     * @param command the logout command containing the refresh token
     * @throws InvalidCredentialsException if the refresh token is invalid
     */
    public void execute(LogoutCommand command) {
        log.info("Processing user logout request");

        // Step 1: Validate that the refresh token is well-formed
        if (!tokenProvider.validateToken(command.refreshToken())) {
            log.warn("Invalid refresh token provided for logout");
            throw new InvalidCredentialsException("Invalid refresh token");
        }
        log.debug("Refresh token validation passed");

        // Step 2: Extract user ID from token for audit purposes
        String userId = tokenProvider.extractUserId(command.refreshToken());
        log.debug("User ID extracted from token: {}", userId);

        // Step 3: Blacklist the refresh token to prevent further use
        blacklistRefreshToken(command.refreshToken());
        log.info("User {} logged out successfully - refresh token blacklisted", userId);
    }

    /**
     * Blacklists a refresh token in the session service cache/store
     * This prevents the token from being used for further token refreshes
     * @param refreshToken the refresh token to blacklist
     */
    private void blacklistRefreshToken(String refreshToken) {
        try {
            log.debug("Blacklisting refresh token");
            sessionService.blacklistRefreshToken(refreshToken, BLACKLIST_DURATION);
            log.debug("Refresh token successfully blacklisted for {} days", BLACKLIST_DURATION.toDays());
        } catch (Exception e) {
            log.error("Failed to blacklist refresh token", e);
            throw new RuntimeException("Failed to complete logout - session invalidation failed", e);
        }
    }
}
