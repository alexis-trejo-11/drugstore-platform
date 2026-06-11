package io.github.alexisTrejo11.drugstore.inventories.integration.support;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * Builds JWTs signed with the same secret as {@code application-test.yml} so requests hit the real
 * {@link libs_kernel.security.jwt.JwtAuthenticationFilter} and {@link libs_kernel.security.jwt.JwtTokenValidator}.
 */
public final class IntegrationTestJwtSupport {

    /**
     * Must match {@code jwt.secret} in {@code src/test/resources/application-test.yml}.
     */
    public static final String SECRET = "integration-test-jwt-secret-key-min-32-bytes!!";

    private static final SecretKey SIGNING_KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private IntegrationTestJwtSupport() {
    }

    public static String accessToken(String userId, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 60 * 60 * 1000))
                .addClaims(Map.of(
                        "userId", userId,
                        "role", role,
                        "type", "access",
                        "email", userId + "@integration.test"
                ))
                .signWith(SIGNING_KEY)
                .compact();
    }

    public static String bearerAdmin() {
        return "Bearer " + accessToken("integration-admin-user", "ADMIN");
    }

    public static String bearerManager() {
        return "Bearer " + accessToken("integration-manager-user", "MANAGER");
    }

    public static String bearerCustomer() {
        return "Bearer " + accessToken("integration-customer-user", "CUSTOMER");
    }
}
