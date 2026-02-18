package microservice.auth_service.app.tokens.interfaces;

public interface NumericToken extends Token {
  String getEmail();

  void setEmail(String email);
}
