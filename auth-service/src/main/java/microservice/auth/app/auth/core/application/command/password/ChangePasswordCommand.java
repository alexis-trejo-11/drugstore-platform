package microservice.auth.app.auth.core.application.command.password;

import microservice.auth.app.auth.core.domain.valueobjects.UserId;

public record ChangePasswordCommand(String token, String newPassword, UserId userId) {
}
