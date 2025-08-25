package microservice.user_service.auth.infrastructure.adapters.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.user_service.auth.core.application.dto.LoginDTO;
import microservice.user_service.auth.core.application.dto.LoginMetadata;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequest {
    @NotNull(message = "Identifier field cannot be null")
    @NotEmpty(message = "Identifier field cannot be empty")
    @Size(min = 3, max = 255, message = "Identifier field must be between 3 and 255 characters")
    private String identifierField; // Can be email or phone number

    @NotNull(message = "Password cannot be null")
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    private String password;

    private String ipAddress;
    private String userAgent;
    private String deviceType;

    public LoginDTO toDTO() {
        LoginMetadata metadata = new LoginMetadata(
                ipAddress != null ? ipAddress : "",
                userAgent != null ? userAgent : "",
                deviceType != null ? deviceType : "");
        return new LoginDTO(identifierField, password, metadata);
    }
}
