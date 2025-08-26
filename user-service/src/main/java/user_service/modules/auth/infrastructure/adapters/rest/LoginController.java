package user_service.modules.auth.infrastructure.adapters.rest;

import user_service.utils.response.Result;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.dto.SessionResponse;
import user_service.modules.auth.core.ports.input.LoginUseCases;
import user_service.modules.auth.infrastructure.adapters.dto.LoginRequest;
import user_service.utils.response.ResponseWrapper;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class LoginController {
    private final LoginUseCases useCases;

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<?>> login(@Valid @RequestBody LoginRequest request) {
        Result<SessionResponse> loginResult = useCases.login(request.toDTO());
        if (!loginResult.isSuccess()) {
            var errorResponse = ResponseWrapper.error(loginResult.getMessage());
            return ResponseEntity.status(loginResult.getStatusCode()).body(errorResponse);
        }

        var response = ResponseWrapper.success(loginResult.getData(), "Login successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("{userId}/login/{code}")
    public ResponseEntity<ResponseWrapper<SessionResponse>> twoFactorLogin(
            @PathVariable UUID userId,
            @PathVariable String code) {
        Result<SessionResponse> sessionResponse = useCases.twoFactorLogin(userId, code);

        if (!sessionResponse.isSuccess()) {
            return ResponseEntity
                    .status(sessionResponse.getStatusCode())
                    .body(ResponseWrapper.error(sessionResponse.getMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.success(sessionResponse.getData(), "2FA Login successful"));
    }

    @PostMapping("/refresh-session/{userId}")
    public ResponseWrapper<SessionResponse> refreshSession(
            @PathVariable UUID userId,
            @RequestParam(required = true) String refreshToken) {

        SessionResponse sessionRefreshed = useCases.refreshSession(userId, refreshToken);
        return ResponseWrapper.success(sessionRefreshed, "Session refreshed successfully");
    }

}
