package user_service.utils.tokens.interfaces;

import java.time.LocalDateTime;

public interface Token {
    String generate();

    boolean validate(String token);

    String getType();

    LocalDateTime expiresAt();
}
