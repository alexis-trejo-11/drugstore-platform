package io.github.alexisTrejo11.drugstore.accounts;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserRole;

@Getter
@Setter
@Builder
public class User {
	private UserId id;
	private Email email;
	private String firstName;
	private String lastName;
	private PhoneNumber phoneNumber;
	private String password;
	private UserRole role;
	private UserStatus status;
	private LocalDateTime lastLoginAt;
	private LocalDateTime createdAt;
	private boolean twoFactorEnabled;

	public void validateUserCanLogin() {
		if (this.getStatus() == UserStatus.PENDING_ACTIVATION) {
			throw new IllegalStateException("User account is pending activation. Please activate your account before logging in.");
		}

		if (this.getStatus() == UserStatus.BANNED) {
			throw new IllegalStateException("User account is banned. Please contact support for assistance.");
		}
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public enum UserStatus {
		ACTIVE,
		PENDING_ACTIVATION,
		INACTIVE,
		BANNED
	}
}
