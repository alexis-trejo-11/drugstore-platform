package microservice.auth.app.auth.core.application.command.twoFa;

import microservice.auth.app.auth.core.domain.valueobjects.UserId;

public record VerifyTwoFactorCommand(UserId userId, String code) {
}
