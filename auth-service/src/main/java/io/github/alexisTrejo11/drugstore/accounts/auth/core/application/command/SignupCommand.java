package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command;

import java.time.LocalDate;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Password;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserRole;


@Builder
public record SignupCommand(
		Email email,
		PhoneNumber phone,
		Password password,
		UserRole role,
		String provider,
		String pictureUrl,
		PersonalInfo personalInfo
		) {
	public SignupCommand {
		if (role == null) {
			throw new IllegalArgumentException("Role cannot be null");
		}
		if (email == null || email.value().trim().isEmpty()) {
			throw new IllegalArgumentException("Email cannot be empty");
		}
		if (phone == null || phone.value().trim().isEmpty()) {
			throw new IllegalArgumentException("Phone cannot be empty");
		}
		if (password == null || password.value().length() < 8) {
			throw new IllegalArgumentException("Password must be at least 8 characters");
		}
	}

	public static SignupCommand of(String email, String phone, String password, UserRole role) {

		return SignupCommand.builder()
				.email(new Email(email))
				.phone(new PhoneNumber(phone))
				.password(new Password(password))
				.role(role)
				.build()
				;
	}

	public record PersonalInfo(String firstName, String lastName, LocalDate dateOfBirth, String gender) {
		public PersonalInfo {
			if (firstName == null || firstName.trim().isEmpty()) {
				throw new IllegalArgumentException("First name cannot be empty");
			}
			if (lastName == null || lastName.trim().isEmpty()) {
				throw new IllegalArgumentException("Last name cannot be empty");
			}
			if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) {
				throw new IllegalArgumentException("Date of birth must be in the past");
			}
		}
	}
}
