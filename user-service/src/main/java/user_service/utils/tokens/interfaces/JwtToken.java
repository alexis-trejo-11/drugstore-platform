package user_service.utils.tokens.interfaces;

import io.jsonwebtoken.Claims;

public interface JwtToken extends Token {
    Claims getClaims();

    String getSubject();
}
