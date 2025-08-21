package microservice.user_service.auth.core.ports.output;

import java.time.Duration;

public interface AuthTokenService {
    String generateRefreshToken(UserClaims claims, Duration expiration);
    String generateAccessToken(Duration expiration);
    void validateToken(String token);
    UserClaims getUserClaims(String token);
}
