package user_service.modules.auth.core.ports.output;

import user_service.utils.tokens.interfaces.TokenType;

public interface TokenRepository {
    void invalidate(String token, String userId);

    boolean isValid(String token);

    void save(String token, String userId, long expiresInMillis, TokenType tokenType);
}
