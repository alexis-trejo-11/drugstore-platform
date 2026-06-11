package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.SignupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Password;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Self-service registration body (role is chosen by URL: customer, employee, admin)")
public record SignupRequest(
    @Schema(description = "Primary email", example = "newuser@example.com")
        @jakarta.validation.constraints.Email
        String email,
    @Schema(description = "National-format phone", example = "+15551234567")
        @Size(min = 6, max = 20)
        String phone,
    @Schema(description = "Password meeting domain policy", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String password,
    @Schema(description = "Given name", requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 3, max = 255)
        String firstName,
    @Schema(description = "Family name", requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 3, max = 255)
        String lastName,
    @Schema(description = "Must be in the past") @Past LocalDate dateOfBirth,
    @Schema(description = "Optional gender label") String gender) {

  public SignupCommand toCommand(UserRole role) {
    return SignupCommand.builder()
        .email(
            new io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Email(email))
        .phone(new PhoneNumber(phone))
        .password(new Password(password))
        .role(role)
        .personalInfo(new SignupCommand.PersonalInfo(firstName, lastName, dateOfBirth, gender))
        .build();
  }
}
