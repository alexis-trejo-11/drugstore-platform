package microservice.user_service.tokens.interfaces;

public interface NumericToken extends Token {
    String getEmail();

    void setEmail(String email);
}
