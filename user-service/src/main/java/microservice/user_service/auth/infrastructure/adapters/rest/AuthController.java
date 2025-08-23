package microservice.user_service.auth.infrastructure.adapters.rest;

import microservice.user_service.utils.response.Result;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import microservice.user_service.auth.core.application.dto.SessionResponse;
import microservice.user_service.auth.core.ports.input.AuthUseCases;
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
        Result<UUID> regiResult = authUseCases.register(request.toDTO(UserRole.CUSTOMER));
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
    public ResponseWrapper<SessionResponse> login() {
        return null;
    }

    @PostMapping("/logout")
    public ResponseWrapper<Void> logout() {
        return null;
    }

    @PostMapping("/logout-all")
    public ResponseWrapper<Void> logoutAll() {
        return null;
    }

    @PostMapping("/refresh-session")
    public ResponseWrapper<SessionResponse> refreshSession() {
        return null;
    }

}
