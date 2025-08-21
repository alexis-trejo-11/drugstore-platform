package microservice.user_service.auth.core.application.dto;

public record LoginMetadata(
        String ipAddress,
        String userAgent,
        String deviceType
) {
    public static LoginMetadata empty() {
        return new LoginMetadata("", "", "");
    }

}
