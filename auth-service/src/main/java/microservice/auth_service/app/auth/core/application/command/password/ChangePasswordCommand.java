package microservice.auth_service.app.auth.core.application.command.password;

import microservice.auth_service.app.auth.core.domain.valueobjects.UserId;

public record ChangePasswordCommand(String token, String newPassword, UserId userId) {
}
