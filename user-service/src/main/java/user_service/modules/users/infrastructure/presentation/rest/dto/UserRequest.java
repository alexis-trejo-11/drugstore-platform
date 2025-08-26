package user_service.modules.users.infrastructure.presentation.rest.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.modules.users.core.application.command.CreateUserCommand;
import user_service.modules.users.core.domain.models.enums.UserRole;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequest {

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    public CreateUserCommand toCommand(UserRole role) {
        return CreateUserCommand.builder()
                .email(email)
                .password(password)
                .role(role)
                .build();
    }
}
