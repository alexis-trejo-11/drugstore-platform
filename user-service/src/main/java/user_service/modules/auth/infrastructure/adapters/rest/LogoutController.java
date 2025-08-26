package user_service.modules.auth.infrastructure.adapters.rest;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.ports.input.LogoutUseCases;
import user_service.utils.response.ResponseWrapper;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class LogoutController {
    private final LogoutUseCases useCases;

    @PostMapping("/logout/{userId}")
    public ResponseWrapper<?> logout(
            @RequestParam(required = true) String refreshToken,
            @PathVariable UUID userId) {
        useCases.logout(refreshToken, userId);
        return ResponseWrapper.success(null, "Logout successfully completed");
    }

    @PostMapping("/logout-all/{userId}")
    public ResponseWrapper<?> logoutAll(@PathVariable UUID userId) {
        useCases.logoutAllSessions(userId);
        return ResponseWrapper.success(null, "Logout successfully completed");
    }

}
