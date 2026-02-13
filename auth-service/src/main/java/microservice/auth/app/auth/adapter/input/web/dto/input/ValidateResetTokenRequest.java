package microservice.auth.app.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.auth.app.auth.core.application.command.ValidateResetTokenCommand;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateResetTokenRequest {
    @NotBlank(message = "Reset token is required")
    private String token;

    public ValidateResetTokenCommand toCommand() {
        return new ValidateResetTokenCommand(this.token);
    }
}