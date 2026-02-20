package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.services;

import java.time.Duration;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.JWTSessions;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.SessionService;

/**
 * Session Manager Service - Provides high-level session management operations.
 * This service orchestrates session lifecycle including creation, validation,
 * refresh, and termination while maintaining security best practices.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionManager {
    
    private final SessionService sessionService;
    
    // Default session configuration
    private static final Duration DEFAULT_REFRESH_TOKEN_TTL = Duration.ofDays(7);
    private static final int MAX_REFRESH_COUNT = 10;
    
    /**
     * Creates and stores a new JWT session for a user.
     * 
     * @param refreshToken the refresh token to associate with the session
     * @param userId the user's ID
     * @return the created JWTSessions object
     */
    @Transactional
    public JWTSessions createSession(String refreshToken, UserId userId) {
        return createSession(refreshToken, userId, DEFAULT_REFRESH_TOKEN_TTL);
    }
    
    /**
     * Creates and stores a new JWT session with custom TTL.
     * 
     * @param refreshToken the refresh token
     * @param userId the user's ID
     * @param ttl time-to-live for the session
     * @return the created JWTSessions object
     */
    @Transactional
    public JWTSessions createSession(String refreshToken, UserId userId, Duration ttl) {
        log.info("Creating new session for user: {}", userId.value());
        
        validateRefreshToken(refreshToken);
        
        if (sessionService.isBlacklisted(refreshToken)) {
            log.warn("Attempted to create session with blacklisted token for user: {}", userId.value());
            throw new IllegalArgumentException("Token is blacklisted");
        }
        
        JWTSessions session = new JWTSessions(refreshToken, userId, ttl);
        sessionService.save(session);
        
        log.info("Session created successfully for user: {}", userId.value());
        return session;
    }
    
    /**
     * Validates and retrieves an active session by refresh token.
     * Checks if the token is blacklisted and if the session is expired.
     * 
     * @param refreshToken the refresh token to validate
     * @return Optional containing the session if valid, empty otherwise
     */
    public Optional<JWTSessions> validateAndGetSession(String refreshToken, String userId) {
        log.debug("Validating session for user: {}", userId);
        
        if (refreshToken == null || refreshToken.isBlank()) {
            log.debug("Invalid refresh token provided");
            return Optional.empty();
        }
        
        // Check blacklist first (fast check)
        if (sessionService.isBlacklisted(refreshToken)) {
            log.warn("Attempted to use blacklisted token for user: {}", userId);
            return Optional.empty();
        }
        
        // Get user's session
        Optional<JWTSessions> sessionOpt = sessionService.findUserJwtSessions(userId);
        
        if (sessionOpt.isEmpty()) {
            log.debug("No active session found for user: {}", userId);
            return Optional.empty();
        }
        
        JWTSessions session = sessionOpt.get();
        
        // Verify token matches
        if (!session.getRefreshToken().equals(refreshToken)) {
            log.warn("Token mismatch for user: {}", userId);
            return Optional.empty();
        }
        
        // Check expiration
        if (session.isExpired()) {
            log.info("Session expired for user: {}", userId);
            invalidateSession(refreshToken, userId);
            return Optional.empty();
        }
        
        // Check refresh count limit
        if (session.getRefreshCount() >= MAX_REFRESH_COUNT) {
            log.warn("Session exceeded maximum refresh count for user: {}", userId);
            invalidateSession(refreshToken, userId);
            return Optional.empty();
        }
        
        log.debug("Session validated successfully for user: {}", userId);
        return Optional.of(session);
    }
    
    /**
     * Refreshes an existing session and creates a new one with a new token.
     * Blacklists the old token and creates a new session.
     * 
     * @param oldRefreshToken the current refresh token
     * @param newRefreshToken the new refresh token to issue
     * @param userId the user's ID
     * @return the new JWTSessions object
     */
    @Transactional
    public JWTSessions refreshSession(String oldRefreshToken, String newRefreshToken, String userId) {
        log.info("Refreshing session for user: {}", userId);
        
        // Validate the old session
        Optional<JWTSessions> oldSessionOpt = validateAndGetSession(oldRefreshToken, userId);
        
        if (oldSessionOpt.isEmpty()) {
            log.warn("Cannot refresh invalid or expired session for user: {}", userId);
            throw new IllegalStateException("Invalid or expired session");
        }
        
        JWTSessions oldSession = oldSessionOpt.get();
        
        // Blacklist the old token
        Duration remainingTtl = Duration.between(
            java.time.LocalDateTime.now(), 
            oldSession.getExpiresAt()
        );
        sessionService.blacklistRefreshToken(oldRefreshToken, remainingTtl);
        
        // Create new session with incremented refresh count
        JWTSessions newSession = new JWTSessions(
            newRefreshToken, 
            oldSession.getUserId(), 
            DEFAULT_REFRESH_TOKEN_TTL
        );
        newSession.setRefreshCount(oldSession.getRefreshCount() + 1);
        newSession.setDeviceInfo(oldSession.getDeviceInfo());
        newSession.setIpAddress(oldSession.getIpAddress());
        
        sessionService.save(newSession);
        
        log.info("Session refreshed successfully for user: {}, refresh count: {}", 
                 userId, newSession.getRefreshCount());
        
        return newSession;
    }
    
    /**
     * Invalidates a single session by blacklisting its refresh token.
     * 
     * @param refreshToken the refresh token to invalidate
     * @param userId the user's ID (for logging purposes)
     */
    @Transactional
    public void invalidateSession(String refreshToken, String userId) {
        log.info("Invalidating session for user: {}", userId);
        
        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("Attempted to invalidate null or blank token");
            return;
        }
        
        // Blacklist with default TTL (7 days to prevent replay)
        sessionService.blacklistRefreshToken(refreshToken, DEFAULT_REFRESH_TOKEN_TTL);
        
        log.info("Session invalidated successfully for user: {}", userId);
    }
    
    /**
     * Invalidates all sessions for a user (e.g., logout from all devices).
     * 
     * @param userId the user's ID
     */
    @Transactional
    public void invalidateAllUserSessions(String userId) {
        log.info("Invalidating all sessions for user: {}", userId);
        
        if (userId == null || userId.isBlank()) {
            log.warn("Attempted to invalidate sessions with null or blank user ID");
            return;
        }
        
        sessionService.blackListAllRefreshTokensForUser(userId);
        
        log.info("All sessions invalidated successfully for user: {}", userId);
    }
    
    /**
     * Checks if a refresh token is currently blacklisted.
     * 
     * @param refreshToken the token to check
     * @return true if blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return true; // Treat invalid tokens as blacklisted
        }
        
        return sessionService.isBlacklisted(refreshToken);
    }
    
    /**
     * Validates refresh token format and content.
     * 
     * @param refreshToken the token to validate
     * @throws IllegalArgumentException if token is invalid
     */
    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token cannot be null or blank");
        }
        
        if (refreshToken.length() < 32) {
            throw new IllegalArgumentException("Refresh token is too short");
        }
    }
}
