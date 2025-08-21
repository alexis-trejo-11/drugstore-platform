package microservice.user_service.users.infrastructure.input.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import microservice.user_service.users.core.application.command.CreateUserCommand;
import microservice.user_service.users.core.application.command.DeleteUserCommand;
import microservice.user_service.users.core.application.command.UpdateUserStatusCommand;
import microservice.user_service.users.core.application.dto.CommandResult;
import microservice.user_service.users.core.application.dto.UserPaginatedResponse;
import microservice.user_service.users.core.application.dto.UserResponse;
import microservice.user_service.users.core.application.queries.GetUserByEmailQuery;
import microservice.user_service.users.core.application.queries.GetUserByIdQuery;
import microservice.user_service.users.core.application.queries.ListUserByRoleQuery;
import microservice.user_service.users.core.application.queries.ListUserByStatusQuery;
import microservice.user_service.users.core.domain.models.enums.UserRole;
import microservice.user_service.users.core.domain.models.enums.UserStatus;
import microservice.user_service.users.core.ports.input.CommandBus;
import microservice.user_service.users.core.ports.input.QueryBus;
import microservice.user_service.users.infrastructure.input.rest.dto.PageRequest;
import microservice.user_service.users.infrastructure.input.rest.dto.UserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequest userRequest) {
        CreateUserCommand command = userRequest.toCommand(UserRole.CUSTOMER);

        CommandResult commandResult = commandBus.dispatch(command);
        if (!commandResult.success()) {
            throw new IllegalArgumentException("Failed to create user: " + commandResult.message());
        }

        return ResponseEntity.status(201).body(commandResult.data());
    }

    @PatchMapping("/{id}/ban")
    public ResponseEntity<Void> banUser(@PathVariable UUID id) {
        UpdateUserStatusCommand command = UpdateUserStatusCommand.ban(id);
        CommandResult result = commandBus.dispatch(command);
        if (!result.success()) {
            throw new IllegalArgumentException("Failed to ban user: " + result.message());
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable UUID id) {
        UpdateUserStatusCommand command = UpdateUserStatusCommand.unban(id);
        CommandResult result = commandBus.dispatch(command);
        if (!result.success()) {
            throw new IllegalArgumentException("Failed to unban user: " + result.message());
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable @NotNull UUID id, @RequestParam String activationCode) {
        if (activationCode == null || activationCode.isBlank()) {
            throw new IllegalArgumentException("Activation code must not be empty");
        }

        UpdateUserStatusCommand command = UpdateUserStatusCommand.activate(id, activationCode);
        CommandResult result = commandBus.dispatch(command);
        if (!result.success()) {
            throw new IllegalArgumentException("Failed to activate user: " + result.message());
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@Valid @PathVariable UUID id) {
        GetUserByIdQuery query = new GetUserByIdQuery(id);

        UserResponse userResponse = queryBus.execute(query);

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponse> getUserById(@Valid @PathVariable @NotNull @Email String email) {
        GetUserByEmailQuery query = new GetUserByEmailQuery(email);

        UserResponse userResponse = queryBus.execute(query);

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("by-role/{role}")
    public ResponseEntity<UserPaginatedResponse> listUserByRole(@PathVariable UserRole role, @ModelAttribute PageRequest pageRequest) {
        ListUserByRoleQuery query = new ListUserByRoleQuery(role, pageRequest.toPageInput());
         UserPaginatedResponse response = queryBus.execute(query);
         return  ResponseEntity.ok(response);
    }

    @GetMapping("by-status/{role}")
    public ResponseEntity<UserPaginatedResponse> listUserByRole(@PathVariable UserStatus userStatus, @ModelAttribute PageRequest pageRequest) {
        ListUserByStatusQuery query = new ListUserByStatusQuery(userStatus, pageRequest.toPageInput());

        UserPaginatedResponse response = queryBus.execute(query);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        DeleteUserCommand command = new DeleteUserCommand(id);

        CommandResult result = commandBus.dispatch(command);
        if (!result.success()) {
            throw new IllegalArgumentException("Failed to delete user: " + result.message());
        }

        return ResponseEntity.noContent().build();
    }

}
