package microservice.auth_service.app.auth.adapter.output.persitence;

import java.util.Map;

import org.springframework.stereotype.Component;

import microservice.auth_service.app.auth.core.domain.valueobjects.Token;
import microservice.auth_service.app.auth.core.domain.valueobjects.UserId;
import microservice.auth_service.app.auth.core.domain.valueobjects.UserRole;
import microservice.auth_service.app.auth.core.ports.output.TokenProvider;

@Component
public class TokenProviderImpl implements TokenProvider {

  @Override
  public Token generateAccessToken(UserId userId, String email, UserRole role) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Token generateRefreshToken(UserId userId, String email) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean validateToken(String token) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String extractUserId(String token) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String extractEmail(String token) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Map<String, Object> extractClaims(String token) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
