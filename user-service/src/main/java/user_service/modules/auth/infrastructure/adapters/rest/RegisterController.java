package user_service.modules.auth.infrastructure.adapters.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.dto.RegisterDTO;
import user_service.modules.auth.core.ports.input.RegisterUseCases;
import user_service.modules.auth.infrastructure.adapters.dto.RegisterRequest;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.utils.response.ResponseWrapper;
import user_service.utils.response.Result;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterUseCases authUseCases;

    @PostMapping("/register-customer")
    public ResponseEntity<ResponseWrapper<?>> registerCustomer(@Valid @RequestBody RegisterRequest request) {
        RegisterDTO dto = request.toDTO(UserRole.CUSTOMER);

        Result<UUID> regiResult = authUseCases.register(dto);
        if (!regiResult.isSuccess()) {
            return ResponseEntity
                    .status(regiResult.getStatusCode())
                    .body(ResponseWrapper.error(regiResult.getMessage()));
        }

        return ResponseEntity
                .status(201)
                .body(ResponseWrapper.created(regiResult.getData(), "Customer User"));
    }

    @PostMapping("/register-customer-oauth2")
    public ResponseEntity<ResponseWrapper<?>> registerOAuth2(@Valid @RequestBody RegisterRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/register-employee")
    public ResponseEntity<ResponseWrapper<?>> registerEmployee(@Valid @RequestBody RegisterRequest request) {
        RegisterDTO dto = request.toDTO(UserRole.EMPLOYEE);

        Result<UUID> regiResult = authUseCases.register(dto);
        if (!regiResult.isSuccess()) {
            return ResponseEntity
                    .status(regiResult.getStatusCode())
                    .body(ResponseWrapper.error(regiResult.getMessage()));
        }

        return ResponseEntity
                .status(201)
                .body(ResponseWrapper.created(regiResult.getData(), "Employee User"));
    }
}
