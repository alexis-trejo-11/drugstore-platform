package user_service.modules.auth.infrastructure.adapters.rest;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.ports.input.TwoFaUseCases;
import user_service.utils.response.ResponseWrapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/2fa")
public class TwoFactorConfigController {
    private final TwoFaUseCases authUseCases;

    @PostMapping("{userId}/enable")
    public ResponseWrapper<UUID> enableTwoFactorAuth(@PathVariable UUID userId) {
        authUseCases.enableTwoFactorAuth(userId);
        return ResponseWrapper.success(userId);
    }

    @PostMapping("{userId}/disable")
    public ResponseWrapper<UUID> disableTwoFactorAuth(@PathVariable UUID userId) {
        authUseCases.disableTwoFactorAuth(userId);
        return ResponseWrapper.success(userId);
    }

    @PostMapping("{userId}/send-code")
    public ResponseWrapper<UUID> sendValidationCode(@PathVariable UUID userId) {
        authUseCases.sendValidationCode(userId);
        return ResponseWrapper.success(userId);
    }

}
