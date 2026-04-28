package io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
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
public @interface UnbanUserOperation {

}
