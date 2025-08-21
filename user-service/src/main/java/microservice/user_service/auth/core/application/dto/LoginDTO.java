package microservice.user_service.auth.core.application.dto;

public record LoginDTO(
        String identifierField,
        String password,
        LoginMetadata metadata
) {
    public static LoginDTO from(String identifierField, String password) {
        return new LoginDTO(identifierField, password, null);
    }
}
