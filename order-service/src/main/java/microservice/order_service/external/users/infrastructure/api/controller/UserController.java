package microservice.order_service.external.users.infrastructure.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import libs_kernel.documentation.SwaggerResponseWrapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.application.service.UserService;
import microservice.order_service.external.users.infrastructure.api.dto.UserInsertRequest;
import microservice.order_service.external.users.infrastructure.api.dto.UserResponse;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users")
@Tag(
        name = "User Management",
        description = "APIs for managing users - create, retrieve, update, and soft delete userID accounts"
)
public class UserController {
    private final UserService userService;
    private final ResponseMapper<UserResponse, User> responseMapper;

    @Operation(
            summary = "Get userID by ID",
            description = "Retrieves a userID by their unique identifier. Returns 404 if userID not found or inactive."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found successfully",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found or inactive",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid userID ID format",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            )
    })
    @Parameter(
            name = "id",
            description = "Unique identifier of the userID",
            example = "550e8400-e29b-41d4-a716-446655440000",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/{id}")
    public ResponseWrapper<UserResponse> getUserById(@PathVariable String id) {
        User user = userService.getUserByID(UserID.of(id));
        UserResponse userResponse = responseMapper.toResponse(user);
        return ResponseWrapper.found(userResponse, "User");
    }

    @Operation(
            summary = "Get userID by email",
            description = "Retrieves a userID by their email address. Returns 404 if userID not found or inactive."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found successfully",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found or inactive",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email format",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            )
    })
    @Parameter(
            name = "email",
            description = "Email address of the userID",
            example = "carol.white@example.com",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/email/{email}")
    public ResponseWrapper<UserResponse> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        UserResponse userResponse = responseMapper.toResponse(user);
        return ResponseWrapper.found(userResponse, "User");
    }

    @Operation(
            summary = "Get userID by phone number",
            description = "Retrieves a userID by their phone number. Returns 404 if userID not found or inactive."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found successfully",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found or inactive",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid phone number format",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            )
    })
    @Parameter(
            name = "phoneNumber",
            description = "Phone number of the userID",
            example = "+1234567890",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/phone/{phoneNumber}")
    public ResponseWrapper<UserResponse> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        User user = userService.getUserByPhoneNumber(phoneNumber);
        UserResponse userResponse = responseMapper.toResponse(user);
        return ResponseWrapper.found(userResponse, "User");
    }

    @Operation(
            summary = "Create a new userID",
            description = "Creates a new userID account with the provided details. Validates email uniqueness and phone number format."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation errors",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with email or phone number already exists",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User creation request",
            required = true,
            content = @Content(schema = @Schema(implementation = UserInsertRequest.class))
    )
    @PostMapping
    public ResponseEntity<ResponseWrapper<UserResponse>> createUser(@Valid @RequestBody UserInsertRequest request) {
        User createdUser = userService.createUser(request.toCommand());
        UserResponse userResponse = responseMapper.toResponse(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(userResponse, "User"));
    }

    @Operation(
            summary = "Update userID details",
            description = "Updates an existing userID's information. Validates email uniqueness (excluding current userID) and maintains data integrity."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation errors",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email or phone number already in use by another userID",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            )
    })
    @Parameters({
            @Parameter(
                    name = "id",
                    description = "Unique identifier of the userID to update",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true,
                    in = ParameterIn.PATH
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User update request with new details",
            required = true,
            content = @Content(schema = @Schema(implementation = UserInsertRequest.class))
    )
    @PutMapping("/{id}")
    public ResponseWrapper<UserResponse> updateUser(@PathVariable String id, @Valid @RequestBody UserInsertRequest request) {
        var command = request.toCommand(id);
        User updatedUser = userService.updateUser(command);
        UserResponse userResponse = responseMapper.toResponse(updatedUser);
        return ResponseWrapper.success(userResponse, "User updated successfully");
    }

    @Operation(
            summary = "Restore soft-deleted userID",
            description = "Restores a previously soft-deleted userID account. The userID becomes active again with all original data intact."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User restored successfully",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User is already active",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            )
    })
    @Parameter(
            name = "id",
            description = "Unique identifier of the userID to restore",
            example = "550e8400-e29b-41d4-a716-446655440000",
            required = true,
            in = ParameterIn.PATH
    )
    @PatchMapping("/{id}/restore")
    public ResponseWrapper<UserResponse> restoreUser(@PathVariable String id) {
        userService.restoreUser(UserID.of(id));
        return ResponseWrapper.success("User restored successfully");
    }

    @Operation(
            summary = "Soft delete userID",
            description = "Performs a soft delete on the userID account. The userID data is preserved but marked as deleted and becomes inactive."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User soft-deleted successfully",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User is already deleted",
                    content = @Content(schema = @Schema(implementation = SwaggerResponseWrapper.class))
            )
    })
    @Parameter(
            name = "id",
            description = "Unique identifier of the userID to delete",
            example = "550e8400-e29b-41d4-a716-446655440000",
            required = true,
            in = ParameterIn.PATH
    )
    @DeleteMapping("/{id}")
    public ResponseWrapper<Void> softDeleteUser(@PathVariable String id) {
        userService.deleteUser(UserID.of(id));
        return ResponseWrapper.success("User deleted successfully");
    }
}