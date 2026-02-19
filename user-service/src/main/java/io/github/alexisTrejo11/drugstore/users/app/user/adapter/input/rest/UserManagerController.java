package io.github.alexisTrejo11.drugstore.users.app.user.adapter.input.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import libs_kernel.response.ResponseWrapper;
import io.github.alexisTrejo11.drugstore.users.app.user.adapter.input.rest.dto.UserRequest;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.command.CreateUserCommand;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.command.DeleteUserCommand;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.command.UpdateUserStatusCommand;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.CommandBus;

@RestController
@RequestMapping("/api/v2/users")
@Tag(name = "User Management", description = "Endpoints for managing user lifecycle operations (create, ban, unban, activate, delete)")
public class UserManagerController {
  private final CommandBus commandBus;

  @Autowired
  public UserManagerController(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Operation(summary = "Create a new user", description = "Registers a new user with CUSTOMER role. Email must be unique and password must meet security requirements (minimum 8 characters).", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Success", value = """
          {
            "message": "User created successfully",
            "data": {
              "userId": "550e8400-e29b-41d4-a716-446655440000",
              "email": "john.doe@example.com",
              "role": "CUSTOMER",
              "status": "PENDING"
            },
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Bad request - Invalid input data (malformed request, validation errors)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Validation Error", value = """
          {
            "message": "Validation failed",
            "error": {
              "code": "VALIDATION_ERROR",
              "details": {
                "email": "Email must be a valid email address",
                "password": "Password must be between 8 and 100 characters"
              }
            },
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Business logic validation failed (e.g., email already exists)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Business Logic Error", value = """
          {
            "message": "User with this email already exists",
            "error": {
              "code": "USER_ALREADY_EXISTS",
              "details": "Email john.doe@example.com is already registered"
            },
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Internal server error - Unexpected error occurred", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Server Error", value = """
          {
            "message": "An unexpected error occurred",
            "error": {
              "code": "INTERNAL_SERVER_ERROR",
              "details": "Unable to process request at this time"
            },
            "timestamp": "2026-02-19T10:30:00"
          }
          """)))
  })
  @PostMapping("/")
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

  @Operation(summary = "Ban a user (Admin only)", description = "Suspends a user account preventing further access. Requires ADMIN role in JWT Bearer token.", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User banned successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Success", value = """
          {
            "message": "User banned successfully",
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Bad request - Invalid user ID format", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication token", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "message": "Unauthorized",
            "error": {
              "code": "UNAUTHORIZED",
              "details": "Valid JWT Bearer token required"
            },
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "403", description = "Forbidden - User lacks ADMIN role required for this operation", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "message": "Forbidden",
            "error": {
              "code": "FORBIDDEN",
              "details": "ADMIN role required for this operation"
            },
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Business logic error (e.g., user not found, already banned)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "message": "User not found or already banned",
            "error": {
              "code": "USER_NOT_FOUND",
              "details": "User with ID 550e8400-e29b-41d4-a716-446655440000 not found"
            },
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
  })
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

  @Operation(summary = "Unban a user (Admin only)", description = "Restores access to a previously banned/suspended user account. Requires ADMIN role in JWT Bearer token.", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User unbanned successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Success", value = """
          {
            "message": "User unbanned successfully",
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Bad request - Invalid user ID format", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication token", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "403", description = "Forbidden - User lacks ADMIN role required for this operation", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Business logic error (e.g., user not found, not banned)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "message": "User is not currently banned",
            "error": {
              "code": "INVALID_OPERATION",
              "details": "Cannot unban a user that is not banned"
            },
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
  })
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

  @Operation(summary = "Activate a user account", description = "Activates a pending user account using the activation code sent to the user's email. Changes status from PENDING to ACTIVE.", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User activated successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Success", value = """
          {
            "message": "User activated successfully",
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Bad request - Invalid user ID or activation code format", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Activation failed (invalid code, expired code, already activated)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "message": "Invalid or expired activation code",
            "error": {
              "code": "INVALID_ACTIVATION_CODE",
              "details": "The activation code is invalid or has expired"
            },
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
  })
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

  @Operation(summary = "Delete a user (Admin only)", description = "Permanently deletes a user account and all associated data. This operation cannot be undone. Requires ADMIN role in JWT Bearer token.", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Success", value = """
          {
            "message": "User deleted successfully",
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Bad request - Invalid user ID format", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication token", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "403", description = "Forbidden - User lacks ADMIN role required for this operation", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Business logic error (e.g., user not found, cannot delete own account)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "message": "User not found",
            "error": {
              "code": "USER_NOT_FOUND",
              "details": "User with ID 550e8400-e29b-41d4-a716-446655440000 does not exist"
            },
            "timestamp": "2026-02-19T10:30:00"
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
  })
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
