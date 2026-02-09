package libs_kernel.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import libs_kernel.security.dto.TokenResponse;
import libs_kernel.security.dto.TokenValidationResponse;
import libs_kernel.security.dto.UserClaims;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

@Service
public class JwtTokenManager {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(JwtTokenManager.class);
	private final JwtProperties jwtProperties;

	public JwtTokenManager(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	public TokenResponse generateTokens(UserClaims user) {
		String accessToken = generateAccessToken(user);
		String refreshToken = generateRefreshToken(user);

		return TokenResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.tokenType("Bearer")
				.expiresIn(jwtProperties.getAccessTokenExpirationSeconds() / 1000)
				.userId(user.userId())
				.role(user.role())
				.build();
	}

	private String generateAccessToken(UserClaims user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", user.role());
		claims.put("userId", user.userId());
		claims.put("email", user.email());
		claims.put("name", user.name());
		claims.put("phoneNumber", user.phoneNumber());
		claims.put("type", "access");

		return buildToken(claims, user.userId(),
				jwtProperties.getAccessTokenExpirationSeconds());
	}

	private String generateRefreshToken(UserClaims user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", user.userId());
		claims.put("type", "refresh");

		return buildToken(claims, user.userId(),
				jwtProperties.getRefreshTokenExpirationSeconds());
	}

	private String buildToken(Map<String, Object> claims, String subject, long expiration) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuer(jwtProperties.getIssuer())
				.setIssuedAt(Date.from(Instant.now()))
				.setExpiration(Date.from(Instant.now().plusMillis(expiration)))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public TokenValidationResponse validateToken(String token) {
		try {
			Claims claims = extractAllClaims(token);

			return TokenValidationResponse.builder()
					.isValid(true)
					.userId(claims.get("userId", String.class))
					.role(claims.get("role", String.class))
					.message("Invalid token")
					.build();
		} catch (Exception e) {
			log.warn("Token validation failed: {}", e.getMessage());
			return TokenValidationResponse.builder()
					.isValid(false)
					.message("Invalid Token " + e.getMessage())
					.build();
		}
	}

	public TokenValidationResponse validateAccessToken(String token) {
		TokenValidationResponse validation = validateToken(token);

		if (!validation.isValid()) {
			return validation;
		}

		try {
			Claims claims = extractAllClaims(token);
			String tokenType = claims.get("type", String.class);

			if (!"access".equals(tokenType)) {
				return TokenValidationResponse.builder()
						.isValid(false)
						.message("Token is not an access token")
						.build();
			}

			return validation;
		} catch (Exception e) {
			return TokenValidationResponse.builder()
					.isValid(false)
					.message("Error validating access token: " + e.getMessage())
					.build();
		}
	}

	public TokenValidationResponse validateRefreshToken(String token) {
		TokenValidationResponse validation = validateToken(token);

		if (!validation.isValid()) {
			return validation;
		}

		try {
			Claims claims = extractAllClaims(token);
			String tokenType = claims.get("type", String.class);

			if (!"refresh".equals(tokenType)) {
				return TokenValidationResponse.builder()
						.isValid(false)
						.message("Token is not a refresh token")
						.build();
			}

			return validation;
		} catch (Exception e) {
			return TokenValidationResponse.builder()
					.isValid(false)
					.message("Error while validating refresh token: " + e.getMessage())
					.build();
		}
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public String extractUserId(String token) {
		return extractAllClaims(token).get("userId", String.class);
	}

	public String extractUserRole(String token) {
		return extractAllClaims(token).get("role", String.class);
	}

	public boolean isTokenExpired(String token) {
		try {
			return extractAllClaims(token).getExpiration().before(new Date());
		} catch (Exception e) {
			return true;
		}
	}
}
