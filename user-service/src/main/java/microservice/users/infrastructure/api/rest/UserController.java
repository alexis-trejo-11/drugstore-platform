package microservice.users.infrastructure.api.rest;

import lombok.RequiredArgsConstructor;
import microservice.users.core.models.User;
import microservice.users.core.models.valueobjects.Email;
import microservice.users.core.models.valueobjects.Password;
import microservice.users.core.models.valueobjects.UserId;
import microservice.users.core.ports.input.UserUseCases;
import microservice.users.infrastructure.api.rest.dto.AuthenticationRequest;
import microservice.users.infrastructure.api.rest.dto.ChangePasswordRequest;
import microservice.users.infrastructure.api.rest.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserUseCases userUseCases;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody User user) {
        try {
            User createdUser = userUseCases.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UserResponse.from(createdUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return userUseCases.getUserById(UserId.of(id))
                .map(user -> ResponseEntity.ok(UserResponse.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userUseCases.getAllUsers().stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody User user) {
        try {
            User updatedUser = userUseCases.updateUser(user);
            return ResponseEntity.ok(UserResponse.from(updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        try {
            userUseCases.deleteUser(UserId.of(id));
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Boolean> authenticateUser(@RequestBody AuthenticationRequest request) {
        boolean isAuthenticated = userUseCases.authenticateUser(
                new Email(request.getEmail()),
                new Password(request.getPassword())
        );
        return ResponseEntity.ok(isAuthenticated);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Boolean> changePassword(@PathVariable UUID id, @RequestBody ChangePasswordRequest request) {
        boolean success = userUseCases.changePassword(
                UserId.of(id),
                new Password(request.getOldPassword()),
                new Password(request.getNewPassword())
        );
        return ResponseEntity.ok(success);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return userUseCases.getUserByEmail(new Email(email))
                .map(user -> ResponseEntity.ok(UserResponse.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}
