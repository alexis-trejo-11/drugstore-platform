package microservice.auth.app.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.auth.app.auth.core.application.command.ForgotPasswordCommand;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    public ForgotPasswordCommand toCommand() {
        return new ForgotPasswordCommand(this.email);
    }
}