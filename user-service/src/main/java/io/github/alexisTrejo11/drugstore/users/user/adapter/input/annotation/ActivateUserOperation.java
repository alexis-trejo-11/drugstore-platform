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
public @interface ActivateUserOperation {

}
