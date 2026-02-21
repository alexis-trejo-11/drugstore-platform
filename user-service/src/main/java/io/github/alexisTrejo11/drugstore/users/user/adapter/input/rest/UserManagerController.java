package io.github.alexisTrejo11.drugstore.users.user.adapter.input.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation.ActivateUserOperation;
import io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation.BanUserOperation;
import io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation.CreateUserOperation;
import io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation.DeleteUserOperation;
import io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation.UnbanUserOperation;
import io.github.alexisTrejo11.drugstore.users.user.adapter.input.rest.dto.UserRequest;
import io.github.alexisTrejo11.drugstore.users.user.core.application.command.CreateUserCommand;
import io.github.alexisTrejo11.drugstore.users.user.core.application.command.DeleteUserCommand;
import io.github.alexisTrejo11.drugstore.users.user.core.application.command.UpdateUserStatusCommand;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.CommandBus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import libs_kernel.response.ResponseWrapper;

@RestController
@RequestMapping("/api/v2/users")
@Tag(name = "User Management", description = "Endpoints for managing user lifecycle operations (create, ban, unban, activate, delete)")
public class UserManagerController {
  private final CommandBus commandBus;

  @Autowired
  public UserManagerController(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @PostMapping("/")
  @CreateUserOperation
  public ResponseEntity<ResponseWrapper<?>> createUser(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration data", required = true, content = @Content(schema = @Schema(implementation = UserRequest.class), examples = @ExampleObject(name = "Create User Request", value = """
          {
            "email": "john.doe@example.com",
            "password": "SecureP@ss123"
          }
          """))) @Valid @RequestBody UserRequest userRequest) {
    CreateUserCommand command = userRequest.toCommand(UserRole.CUSTOMER);

    CommandResult commandResult = commandBus.dispatch(command);

    var response = ResponseWrapper.success(commandResult.data(), "User created successfully");
    return ResponseEntity.status(201).body(response);
  }

  @BanUserOperation
  @PatchMapping("/{id}/ban")
  public ResponseWrapper<Void> banUser(
      @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable String id) {
    UpdateUserStatusCommand command = UpdateUserStatusCommand.ban(id);

    CommandResult result = commandBus.dispatch(command);
    if (!result.success()) {
      throw new IllegalArgumentException("Failed to ban user: " + result.message());
    }

    return ResponseWrapper.success("User banned successfully");
  }

  @UnbanUserOperation
  @PatchMapping("/{id}/unban")
  public ResponseWrapper<Void> unbanUser(
      @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable String id) {
    UpdateUserStatusCommand command = UpdateUserStatusCommand.unban(id);

    CommandResult result = commandBus.dispatch(command);
    if (!result.success()) {
      throw new IllegalArgumentException("Failed to unban user: " + result.message());
    }

    return ResponseWrapper.success("User unbanned successfully");
  }

  @ActivateUserOperation
  @PatchMapping("/{id}/activate/code/{activationCode}")
  public ResponseWrapper<Void> activateUser(
      @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable @Valid @NotBlank String id,
      @Parameter(description = "Activation code sent to user's email", required = true, example = "ABC123XYZ789") @PathVariable @NotBlank String activationCode) {
    UpdateUserStatusCommand command = UpdateUserStatusCommand.activate(id, activationCode);

    CommandResult result = commandBus.dispatch(command);
    if (!result.success()) {
      throw new IllegalArgumentException("Failed to activate user: " + result.message());
    }

    return ResponseWrapper.success("User activated successfully");
  }

  @DeleteUserOperation
  @DeleteMapping("/{id}")
  public ResponseWrapper<Void> deleteUser(
      @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable String id) {
    DeleteUserCommand command = DeleteUserCommand.of(id);
    CommandResult result = commandBus.dispatch(command);
    if (!result.success()) {
      throw new IllegalArgumentException("Failed to delete user: " + result.message());
    }

    return ResponseWrapper.success("User deleted successfully");
  }

}
