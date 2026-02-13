package microservice.auth.app.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.auth.app.auth.core.application.command.ChangePasswordCommand;
import microservice.auth.app.auth.core.domain.valueobjects.UserId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    public ChangePasswordCommand toCommand(String userId) {
        return ChangePasswordCommand.builder()
                .userId(new UserId(userId))
                .currentPassword(this.currentPassword)
                .newPassword(this.newPassword)
                .confirmPassword(this.confirmPassword)
                .build();
    }
}