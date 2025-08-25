package microservice.user_service.auth.infrastructure.adapters.rest;

import microservice.user_service.utils.response.Result;

import java.util.UUID;

import org.apache.catalina.connector.Response;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import microservice.user_service.auth.core.application.dto.RegisterDTO;
import microservice.user_service.auth.core.application.dto.SessionResponse;
import microservice.user_service.auth.core.ports.input.AuthUseCases;
import microservice.user_service.auth.infrastructure.adapters.dto.LoginRequest;
import microservice.user_service.auth.infrastructure.adapters.dto.RegisterRequest;
import microservice.user_service.users.core.domain.models.enums.UserRole;
import microservice.user_service.utils.response.ResponseWrapper;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthUseCases authUseCases;

    @PostMapping("/register-customer")
    public ResponseEntity<ResponseWrapper<?>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterDTO dto = request.toDTO(UserRole.CUSTOMER);

        Result<UUID> regiResult = authUseCases.register(dto);
        if (!regiResult.isSuccess()) {
            return ResponseEntity
                    .status(regiResult.getStatusCode())
                    .body(ResponseWrapper.error(regiResult.getMessage()));
        }

        return ResponseEntity
                .status(201)
                .body(ResponseWrapper.created(regiResult.getData(), "User"));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<?>> login(@Valid @RequestBody LoginRequest request) {
        Result<SessionResponse> loginResult = authUseCases.login(request.toDTO());
        if (!loginResult.isSuccess()) {
            var errorResponse = ResponseWrapper.error(loginResult.getMessage());
            return ResponseEntity.status(loginResult.getStatusCode()).body(errorResponse);
        }

        var response = ResponseWrapper.success(loginResult.getData(), "Login successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout/{userId}")
    public ResponseEntity<ResponseWrapper<?>> logout(@RequestParam(required = true) String refreshToken,
            @PathVariable UUID userId) {
        authUseCases.logout(refreshToken, userId);

        var response = ResponseWrapper.success(null, "Logout successfully completed");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout-all/{userId}")
    public ResponseEntity<ResponseWrapper<?>> logoutAll(@PathVariable UUID userId) {
        authUseCases.logoutAllSessions(userId);

        var response = ResponseWrapper.success(null, "Logout successfully completed");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-session/{userId}")
    public ResponseEntity<ResponseWrapper<SessionResponse>> refreshSession(
            @PathVariable UUID userId,
            @RequestParam(required = true) String refreshToken) {

        System.out.println("Received refresh token: " + refreshToken);
        var sessionRefreshed = authUseCases.refreshSession(userId, refreshToken);

        var response = ResponseWrapper.success(sessionRefreshed, "Session refreshed successfully");
        return ResponseEntity.ok(response);
    }

}
