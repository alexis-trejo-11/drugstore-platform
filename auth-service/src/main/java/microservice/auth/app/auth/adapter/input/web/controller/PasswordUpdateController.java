package microservice.auth.app.auth.adapter.input.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.response.ResponseWrapper;
import microservice.auth.app.auth.adapter.input.web.dto.input.ChangePasswordRequest;
import microservice.auth.app.auth.core.application.command.ChangePasswordCommand;
import microservice.auth.app.auth.core.ports.input.PasswordUpdateUseCases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/auth/password")
public class PasswordUpdateController {
    private final PasswordUpdateUseCases passwordUpdateUseCases;

    @Autowired
    public PasswordUpdateController(PasswordUpdateUseCases passwordUpdateUseCases) {
        this.passwordUpdateUseCases = passwordUpdateUseCases;
    }

    @PutMapping("/change")
    public ResponseEntity<ResponseWrapper<Void>> changePassword(
            @RequestAttribute("userId") String userId,
            @RequestBody @Valid @NotNull ChangePasswordRequest request) {
        ChangePasswordCommand command = request.toCommand(userId);
        passwordUpdateUseCases.changePassword(command);

        return ResponseEntity.ok(
                ResponseWrapper.success(null, "Password changed successfully")
        );
    }
}