package io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import libs_kernel.response.ResponseWrapper;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Get user by email", description = "Retrieves user information by their email address. Email lookup is case-insensitive.", security = @SecurityRequirement(name = "bearerAuth"))
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "User found and retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Success", value = """
        {
          "message": "User retrieved successfully",
          "data": {
            "id": "550e8400-e29b-41d4-a716-446655440000",
            "email": "john.doe@example.com",
            "phoneNumber": "+1-555-123-4567",
            "role": "CUSTOMER",
            "joinedAt": "2026-01-15T10:30:00",
            "lastLoginAt": "2026-02-19T09:15:00"
          },
          "timestamp": "2026-02-19T10:30:00"
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid email format", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "Invalid email format",
          "error": {
            "code": "INVALID_EMAIL",
            "details": "Email must be a valid email address"
          },
          "timestamp": "2026-02-19T10:30:00"
        }
        """))),
    @ApiResponse(responseCode = "422", description = "Unprocessable Entity - User with email not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "User not found",
          "error": {
            "code": "USER_NOT_FOUND",
            "details": "No user exists with email john.doe@example.com"
          },
          "timestamp": "2026-02-19T10:30:00"
        }
        """))),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
})
public @interface GetUserByEmailOperation {

}
