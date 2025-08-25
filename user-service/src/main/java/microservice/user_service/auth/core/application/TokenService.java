package microservice.user_service.auth.core.application;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import microservice.user_service.auth.core.ports.output.UserClaims;
import microservice.user_service.utils.tokens.factory.TokenFactory;
import microservice.user_service.utils.tokens.interfaces.Token;
import microservice.user_service.utils.tokens.interfaces.TokenType;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenFactory tokenFactory;

    public Token createToken(UserClaims userClaims, TokenType tokenType) {
        Token token = tokenFactory.createToken(tokenType, tokenType.name(), userClaims.toMap());
        return token;
    }

    public UserClaims parseToken(String token, TokenType tokenType, String userId) {
        return null;

    }

    public void validateToken(String token, TokenType tokenType, String userId) {
    }
}
