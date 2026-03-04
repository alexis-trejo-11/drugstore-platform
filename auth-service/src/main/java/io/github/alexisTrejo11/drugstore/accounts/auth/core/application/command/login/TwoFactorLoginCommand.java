package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;
import lombok.Builder;

@Builder
public record TwoFactorLoginCommand(
    String email,
    String code,
    String deviceId,
    String ipAddress) {

}
