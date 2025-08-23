package microservice.user_service.tokens.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import microservice.user_service.tokens.interfaces.Token;
import microservice.user_service.tokens.interfaces.TokenType;

@Component
@RequiredArgsConstructor
public class TokenFactory {
    private final AccessToken accessToken;
    private final RefreshToken refreshToken;
    private final ActivationToken activationToken;
    private final TwoFaToken twoFAToken;

    public Token createToken(TokenType type, String subject, Map<String, Object> claims) {
        return switch (type) {
            case ACCESS -> {
                accessToken.setSubject(subject);
                accessToken.setClaims(claims);
                yield accessToken;
            }
            case REFRESH -> {
                refreshToken.setSubject(subject);
                yield refreshToken;
            }
            case ACTIVATION -> {
                activationToken.setEmail((String) claims.get("email"));
                yield activationToken;
            }
            case TWO_FA -> twoFAToken;
        };
    }

    public Token createToken(TokenType type) {
        return createToken(type, null, Map.of());
    }
}
