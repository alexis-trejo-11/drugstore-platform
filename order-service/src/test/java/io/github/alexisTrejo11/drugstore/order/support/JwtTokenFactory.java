package io.github.alexisTrejo11.drugstore.order.support;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Issues access tokens compatible with {@link libs_kernel.security.jwt.JwtTokenValidator}.
 */
public final class JwtTokenFactory {

	private JwtTokenFactory() {
	}

	public static String accessToken(String secret, String userId, String role) {
		SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		long now = System.currentTimeMillis();
		return Jwts.builder()
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(new Date(now))
				.setExpiration(new Date(now + 60 * 60 * 1000L))
				.claim("userId", userId)
				.claim("role", role)
				.claim("type", "access")
				.claim("email", "integration-test@example.com")
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
}
