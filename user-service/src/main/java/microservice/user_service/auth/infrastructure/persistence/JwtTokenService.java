package microservice.user_service.auth.infrastructure.persistence;

import java.time.Duration;

import org.springframework.stereotype.Service;

import microservice.user_service.auth.core.ports.output.AuthTokenService;
import microservice.user_service.auth.core.ports.output.UserClaims;

@Service
public class JwtTokenService implements AuthTokenService {

    @Override
    public String generateRefreshToken(UserClaims claims, Duration expiration) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateRefreshToken'");
    }

    @Override
    public String generateAccessToken(Duration expiration) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateAccessToken'");
    }

    @Override
    public void validateToken(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateToken'");
    }

    @Override
    public UserClaims getUserClaims(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserClaims'");
    }

}
