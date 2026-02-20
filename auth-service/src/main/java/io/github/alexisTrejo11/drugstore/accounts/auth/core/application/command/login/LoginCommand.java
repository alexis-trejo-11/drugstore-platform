package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login;

import lombok.Builder;

@Builder
public record LoginCommand(
		String identifier, // email or phoneNumber
		String password,
		String deviceId,
		String deviceName,
		String ipAddress
) {

	public LoginCommand{
		if (identifier == null || identifier.isBlank()) {
			throw new IllegalArgumentException("Identifier cannot be null or blank");
		}
		if (password == null || password.isBlank()) {
			throw new IllegalArgumentException("Password cannot be null or blank");
		}
	}
}
