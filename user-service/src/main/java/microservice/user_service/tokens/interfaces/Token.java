package microservice.user_service.tokens.interfaces;

public interface Token {
    String generate();

    boolean validate(String token);

    String getType();
}
