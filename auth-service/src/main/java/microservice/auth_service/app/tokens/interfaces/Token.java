package microservice.auth_service.app.tokens.interfaces;

import java.time.LocalDateTime;

public interface Token {
  String generate();

  boolean validate(String token);

  String getType();

  LocalDateTime expiresAt();
}
