package microservice.auth_service.app.auth.core.application.command.twoFa;

import microservice.auth_service.app.auth.core.domain.valueobjects.UserId;

public record VerifyTwoFactorCommand(UserId userId, String code) {
}
