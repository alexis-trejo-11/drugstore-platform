package user_service.modules.users.infrastructure.presentation.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import user_service.modules.users.core.application.command.CreateUserCommand;
import user_service.modules.users.core.application.command.DeleteUserCommand;
import user_service.modules.users.core.application.command.UpdateUserStatusCommand;
import user_service.modules.users.core.application.dto.CommandResult;
import user_service.modules.users.core.application.dto.UserPaginatedResponse;
import user_service.modules.users.core.application.dto.UserResponse;
import user_service.modules.users.core.application.queries.GetUserByEmailQuery;
import user_service.modules.users.core.application.queries.GetUserByIdQuery;
import user_service.modules.users.core.application.queries.GetUserByPhoneNumberQuery;
import user_service.modules.users.core.application.queries.ListUserByRoleQuery;
import user_service.modules.users.core.application.queries.ListUserByStatusQuery;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.enums.UserStatus;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;
import user_service.modules.users.core.ports.input.CommandBus;
import user_service.modules.users.core.ports.input.QueryBus;
import user_service.modules.users.infrastructure.presentation.rest.dto.PageRequest;
import user_service.modules.users.infrastructure.presentation.rest.dto.UserRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
public class UserController {
    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @PostMapping("/")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequest userRequest) {
        CreateUserCommand command = userRequest.toCommand(UserRole.CUSTOMER);

        CommandResult commandResult = commandBus.dispatch(command);

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
    public ResponseEntity<UserResponse> getUserById(@Valid @PathVariable @NotNull String email) {
        GetUserByEmailQuery query = new GetUserByEmailQuery(email);

        UserResponse userResponse = queryBus.execute(query);

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("by-phone/{phone}")
    public ResponseEntity<UserResponse> getUserByPhone(@Valid @PathVariable @NotNull String phone) {
        GetUserByPhoneNumberQuery query = new GetUserByPhoneNumberQuery(PhoneNumber.of(phone));

        UserResponse userResponse = queryBus.execute(query);

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("by-role/{role}")
    public ResponseEntity<UserPaginatedResponse> listUserByRole(@PathVariable UserRole role,
            @ModelAttribute PageRequest pageRequest) {
        ListUserByRoleQuery query = new ListUserByRoleQuery(role, pageRequest.toPageInput());
        UserPaginatedResponse response = queryBus.execute(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("by-status/{status}")
    public ResponseEntity<UserPaginatedResponse> listUserByRole(@PathVariable UserStatus status,
            @ModelAttribute PageRequest pageRequest) {
        ListUserByStatusQuery query = new ListUserByStatusQuery(status, pageRequest.toPageInput());

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
