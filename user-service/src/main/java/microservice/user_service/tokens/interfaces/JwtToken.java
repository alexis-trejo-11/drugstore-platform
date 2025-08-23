package microservice.user_service.tokens.interfaces;

import io.jsonwebtoken.Claims;

public interface JwtToken extends Token {
    Claims getClaims();

    String getSubject();
}
