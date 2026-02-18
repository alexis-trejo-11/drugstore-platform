package microservice.auth_service.app.tokens.interfaces;

import io.jsonwebtoken.Claims;

public interface JwtToken extends Token {
  Claims getClaims();

  String getSubject();
}
