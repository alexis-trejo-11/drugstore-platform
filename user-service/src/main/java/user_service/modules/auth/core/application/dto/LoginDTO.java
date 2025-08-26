package user_service.modules.auth.core.application.dto;

public record LoginDTO(
        String identifierField,
        String password,
        LoginMetadata metadata) {
    public static LoginDTO from(String identifierField, String password) {
        LoginMetadata emptyMetadata = LoginMetadata.empty();
        return new LoginDTO(identifierField, password, emptyMetadata);
    }
}
