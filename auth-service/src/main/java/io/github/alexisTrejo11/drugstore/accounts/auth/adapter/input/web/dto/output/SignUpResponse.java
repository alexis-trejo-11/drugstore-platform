package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SignUpResult;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private LocalDateTime createdAt;
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