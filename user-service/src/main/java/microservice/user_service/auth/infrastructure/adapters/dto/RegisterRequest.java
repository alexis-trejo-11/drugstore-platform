package microservice.user_service.auth.infrastructure.adapters.dto;

import java.time.LocalDate;

import org.checkerframework.checker.units.qual.N;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.user_service.auth.core.application.dto.RegisterDTO;
import microservice.user_service.users.core.domain.models.enums.Gender;
import microservice.user_service.users.core.domain.models.enums.UserRole;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {

    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @NotNull(message = "Password cannot be null")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 3, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 3, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @NotBlank(message = "Phone number cannot be blank")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phoneNumber;

    @NotNull(message = "Birth date cannot be null")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotNull(message = "Gender cannot be null")
    private Gender gender;

    public RegisterDTO toDTO(UserRole userRole) {
        return new RegisterDTO(
                this.email,
                this.phoneNumber,
                this.password,
                userRole);
    }
}
