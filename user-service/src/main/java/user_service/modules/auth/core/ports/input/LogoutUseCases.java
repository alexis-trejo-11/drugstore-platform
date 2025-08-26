package user_service.modules.auth.core.ports.input;

import java.util.UUID;

public interface LogoutUseCases {
    void logout(String token, UUID userId);

    void logoutAllSessions(UUID userId);

}
