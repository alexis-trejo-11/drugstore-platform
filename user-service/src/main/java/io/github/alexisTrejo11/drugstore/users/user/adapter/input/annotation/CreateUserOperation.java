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
public @interface CreateUserOperation {

}
