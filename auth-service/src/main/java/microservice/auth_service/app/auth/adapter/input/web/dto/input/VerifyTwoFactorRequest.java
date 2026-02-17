package microservice.auth_service.app.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyTwoFactorRequest {
    @NotBlank(message = "Verification code is required")
    @Size(min = 6, max = 6, message = "Code must be 6 digits")
    private String code;
}
