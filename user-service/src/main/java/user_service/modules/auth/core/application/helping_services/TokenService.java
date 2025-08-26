package user_service.modules.auth.core.application.helping_services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.domain.session.UserClaims;
import user_service.utils.tokens.factory.TokenFactory;
import user_service.utils.tokens.interfaces.Token;
import user_service.utils.tokens.interfaces.TokenType;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenFactory tokenFactory;

    public Token createToken(UserClaims userClaims, String subject, TokenType tokenType) {
        Token token = tokenFactory.createToken(tokenType, subject, userClaims.toMap());
        return token;
    }

    public UserClaims parseToken(String token, TokenType tokenType, String userId) {
        return null;

    }

    public boolean validateToken(String token, TokenType tokenType, UUID userId) {
        return true;
    }
}
