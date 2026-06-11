package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SignUpResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Newly registered user summary returned after successful sign-up")
public class SignUpResponse {
    @Schema(description = "Platform user identifier")
    private String userId;
    @Schema(description = "Registered email address")
    private String email;
    @Schema(description = "Given name")
    private String firstName;
    @Schema(description = "Family name")
    private String lastName;
    @Schema(description = "Assigned role", example = "CUSTOMER")
    private String role;
    @Schema(description = "Registration timestamp")
    private LocalDateTime createdAt;
    @Schema(description = "Whether the user must verify email before full access")
    private Boolean requiresEmailVerification;

    public static SignUpResponse fromResult(SignUpResult result) {
        return SignUpResponse.builder()
                .userId(result.userId() != null ? result.userId().value() : null)
                .email(result.email())
                .firstName(result.firstName())
                .lastName(result.lastName())
                .role(result.role() != null ? result.role().name() : null)
                .createdAt(result.createdAt())
                .requiresEmailVerification(result.requiresEmailVerification())
                .build();
    }
}