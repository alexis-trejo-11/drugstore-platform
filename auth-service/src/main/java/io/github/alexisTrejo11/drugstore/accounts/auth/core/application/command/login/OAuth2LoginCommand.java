package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.OAuth2Provider;

@Builder
public record OAuth2LoginCommand(
		String token,
		OAuth2Provider provider,
		String deviceId,
		String deviceName,
		String ipAddress
) {
}
