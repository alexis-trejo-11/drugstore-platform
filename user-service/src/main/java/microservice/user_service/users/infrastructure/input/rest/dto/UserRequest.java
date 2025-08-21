package microservice.user_service.users.infrastructure.input.rest.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import microservice.user_service.users.core.application.command.CreateUserCommand;
import microservice.user_service.users.core.domain.models.enums.UserRole;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @Size(min = 3, max = 50, message = "First must be between 3 and 50 characters")
    private String firstName;

    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private String lastName;

    @NotNull(message = "BirthDate cannot be null")
    @Past(message = "BirthDate must be a date in the past")
    private Date birthDate;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotNull(message = "Phone number cannot be null")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    public CreateUserCommand toCommand (UserRole role) {
        return CreateUserCommand.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(birthDate)
                .email(email)
                .password(password)
                .role(role)
                .build();
    }
}
