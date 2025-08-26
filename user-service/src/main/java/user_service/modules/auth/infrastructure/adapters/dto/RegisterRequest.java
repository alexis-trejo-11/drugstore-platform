package user_service.modules.auth.infrastructure.adapters.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.modules.auth.core.application.dto.CreateProfileDTO;
import user_service.modules.auth.core.application.dto.RegisterDTO;
import user_service.modules.users.core.domain.models.enums.UserRole;

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

    @NotBlank(message = "Phone number cannot be blank")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phoneNumber;

    @NotNull(message = "Profile cannot be null")
    private CreateProfileDTO profile;

    public RegisterDTO toDTO(UserRole userRole) {
        return new RegisterDTO(
                this.email,
                this.phoneNumber,
                this.password,
                userRole,
                profile);
    }
}
