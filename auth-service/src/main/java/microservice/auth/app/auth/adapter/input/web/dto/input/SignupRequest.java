package microservice.auth.app.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import microservice.auth.app.auth.core.application.command.SignupCommand;
import microservice.auth.app.auth.core.domain.valueobjects.UserRole;

import java.time.LocalDate;

public record SignupRequest(
		@Email String email,
		@Size(min = 6, max = 20) String phone,
		@NotBlank String password,
		@Size(min = 3, max = 255) String firstName,
		@Size(min = 3, max = 255) String lastName,
		@Past LocalDate dateOfBirth,
		String gender
) {
	public SignupCommand toCommand(UserRole role) {
		return SignupCommand.of(
				email,
				phone,
				password,
				role
		);
	}
}
