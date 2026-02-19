package io.github.alexisTrejo11.drugstore.users.app.user.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.command.CreateUserCommand;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserRole;

/**
 * Data Transfer Object for user registration/creation requests.
 * Contains the minimal information required to create a new user account.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Request payload for creating a new user account")
public class UserRequest {

  @Schema(description = "User's email address (must be unique in the system)", example = "john.doe@example.com", format = "email", maxLength = 255)
  @NotNull(message = "Email cannot be null")
  @Email(message = "Email must be a valid email address")
  private String email;

  @Schema(description = "User's password (must meet security requirements)", example = "SecureP@ss123", minLength = 8, maxLength = 100, format = "password")
  @NotNull(message = "Password cannot be null")
  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
  private String password;

  public CreateUserCommand toCommand(UserRole role) {
    return CreateUserCommand.builder()
        .email(new io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.Email(this.email))
        .password(password)
        .role(role)
        .build();
  }
}
