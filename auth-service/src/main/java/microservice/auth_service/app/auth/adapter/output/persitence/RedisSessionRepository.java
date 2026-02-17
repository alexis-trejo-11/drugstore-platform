package microservice.auth_service.app.auth.adapter.output.persitence;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.auth_service.app.auth.core.domain.models.JWTSessions;
import microservice.auth_service.app.auth.core.ports.output.SessionService;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisSessionRepository implements SessionService {
	private final RedisTemplate<String, Object> redisTemplate;

	// Key prefixes for different data structures
	private static final String SESSION_PREFIX = "session:";
	private static final String BLACKLIST_PREFIX = "blacklist:token:";
	private static final String USER_SESSIONS_PREFIX = "user:sessions:";

	/**
	 * Saves a JWT session to Redis with automatic expiration.
	 * Also maintains a set of active sessions per user for easy tracking.
	 * 
	 * @param session the JWT session to save
	 */
	@Override
	public void save(JWTSessions session) {
		if (session == null || session.getRefreshToken() == null) {
			log.warn("Attempted to save null session or session with null refresh token");
			return;
		}

		try {
			String sessionKey = SESSION_PREFIX + session.getRefreshToken();
			String userSessionsKey = USER_SESSIONS_PREFIX + session.getUserIdValue();

			Duration ttl = Duration.between(LocalDateTime.now(), session.getExpiresAt());

			if (ttl.isNegative() || ttl.isZero()) {
				log.warn("Session already expired for token: {}", maskToken(session.getRefreshToken()));
				return;
			}

			// Save the session object
			redisTemplate.opsForValue().set(sessionKey, session, ttl);

			// Add refresh token to user's active sessions set
			redisTemplate.opsForSet().add(userSessionsKey, session.getRefreshToken());
			redisTemplate.expire(userSessionsKey, ttl);

			log.info("Session saved successfully for user: {} with token: {}, expires in: {} seconds",
					session.getUserIdValue(),
					maskToken(session.getRefreshToken()),
					ttl.getSeconds());

		} catch (Exception e) {
			log.error("Error saving session for user: {}", session.getUserIdValue(), e);
			throw new RuntimeException("Failed to save session to Redis", e);
		}
	}

	/**
	 * Finds a JWT session by user ID.
	 * Returns the most recently refreshed session if multiple exist.
	 * 
	 * @param userId the user ID
	 * @return Optional containing the session if found
	 */
	@Override
	public Optional<JWTSessions> findUserJwtSessions(String userId) {
		if (userId == null || userId.isBlank()) {
			log.warn("Attempted to find sessions with null or blank user ID");
			return Optional.empty();
		}

		try {
			String userSessionsKey = USER_SESSIONS_PREFIX + userId;
			Set<Object> refreshTokens = redisTemplate.opsForSet().members(userSessionsKey);

			if (refreshTokens == null || refreshTokens.isEmpty()) {
				log.debug("No active sessions found for user: {}", userId);
				return Optional.empty();
			}

			log.debug("Found {} active session(s) for user: {}", refreshTokens.size(), userId);

			// Get all sessions and find the most recently refreshed one
			Optional<JWTSessions> mostRecentSession = refreshTokens.stream()
					.map(token -> {
						String sessionKey = SESSION_PREFIX + token;
						return (JWTSessions) redisTemplate.opsForValue().get(sessionKey);
					})
					.filter(session -> session != null && !session.isExpired())
					.max((s1, s2) -> s1.getLastRefreshAt().compareTo(s2.getLastRefreshAt()));

			if (mostRecentSession.isPresent()) {
				log.debug("Retrieved most recent session for user: {}", userId);
			} else {
				log.debug("All sessions expired or invalid for user: {}", userId);
			}

			return mostRecentSession;

		} catch (Exception e) {
			log.error("Error finding sessions for user: {}", userId, e);
			return Optional.empty();
		}
	}

	/**
	 * Blacklists a specific refresh token to prevent its reuse.
	 * 
	 * @param refreshToken the token to blacklist
	 * @param duration     how long to keep the token blacklisted
	 */
	@Override
	public void blacklistRefreshToken(String refreshToken, Duration duration) {
		if (refreshToken == null || refreshToken.isBlank()) {
			log.warn("Attempted to blacklist null or blank refresh token");
			return;
		}

		try {
			String blacklistKey = BLACKLIST_PREFIX + refreshToken;
			redisTemplate.opsForValue().set(blacklistKey, true, duration);

			log.info("Refresh token blacklisted: {}, expires in: {} seconds",
					maskToken(refreshToken), duration.getSeconds());

		} catch (Exception e) {
			log.error("Error blacklisting refresh token: {}", maskToken(refreshToken), e);
			throw new RuntimeException("Failed to blacklist token", e);
		}
	}

	/**
	 * Checks if a refresh token is blacklisted.
	 * 
	 * @param refreshToken the token to check
	 * @return true if blacklisted, false otherwise
	 */
	@Override
	public boolean isBlacklisted(String refreshToken) {
		if (refreshToken == null || refreshToken.isBlank()) {
			log.warn("Attempted to check blacklist status with null or blank token");
			return false;
		}

		try {
			String blacklistKey = BLACKLIST_PREFIX + refreshToken;
			Boolean isBlacklisted = (Boolean) redisTemplate.opsForValue().get(blacklistKey);
			boolean result = isBlacklisted != null && isBlacklisted;

			if (result) {
				log.debug("Token is blacklisted: {}", maskToken(refreshToken));
			}

			return result;

		} catch (Exception e) {
			log.error("Error checking blacklist status for token: {}", maskToken(refreshToken), e);
			// Fail secure: treat as blacklisted if we can't verify
			return true;
		}
	}

	/**
	 * Blacklists all active refresh tokens for a specific user.
	 * Useful for logout all sessions or security incidents.
	 * 
	 * @param userId the user ID whose tokens should be blacklisted
	 */
	@Override
	public void blackListAllRefreshTokensForUser(String userId) {
		if (userId == null || userId.isBlank()) {
			log.warn("Attempted to blacklist all tokens with null or blank user ID");
			return;
		}

		try {
			String userSessionsKey = USER_SESSIONS_PREFIX + userId;
			Set<Object> refreshTokens = redisTemplate.opsForSet().members(userSessionsKey);

			if (refreshTokens == null || refreshTokens.isEmpty()) {
				log.info("No active sessions to blacklist for user: {}", userId);
				return;
			}

			log.info("Blacklisting {} session(s) for user: {}", refreshTokens.size(), userId);

			// Blacklist each token and remove the session
			int blacklistedCount = 0;
			for (Object tokenObj : refreshTokens) {
				if (tokenObj instanceof String token) {
					try {
						// Get session to determine TTL
						String sessionKey = SESSION_PREFIX + token;
						JWTSessions session = (JWTSessions) redisTemplate.opsForValue().get(sessionKey);

						if (session != null && !session.isExpired()) {
							Duration remainingTtl = Duration.between(LocalDateTime.now(), session.getExpiresAt());

							if (remainingTtl.isPositive()) {
								blacklistRefreshToken(token, remainingTtl);
								blacklistedCount++;
							}
						}

						// Delete the session
						redisTemplate.delete(sessionKey);

					} catch (Exception e) {
						log.error("Error blacklisting individual token for user: {}", userId, e);
					}
				}
			}

			// Clear the user's session set
			redisTemplate.delete(userSessionsKey);

			log.info("Successfully blacklisted {} out of {} tokens for user: {}",
					blacklistedCount, refreshTokens.size(), userId);

		} catch (Exception e) {
			log.error("Error blacklisting all tokens for user: {}", userId, e);
			throw new RuntimeException("Failed to blacklist all user tokens", e);
		}
	}

	/**
	 * Masks a token for secure logging (shows only first and last 4 characters).
	 * 
	 * @param token the token to mask
	 * @return masked token string
	 */
	private String maskToken(String token) {
		if (token == null || token.length() < 8) {
			return "***";
		}
		return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
	}
}
