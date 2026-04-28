package io.github.alexisTrejo11.drugstore.address.controller.annotation;

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

@Operation(summary = "Update any address", description = "Updates an existing address by ID (Admin only)")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Address updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "id": "123e4567-e89b-12d3-a456-426614174000",
          "userId": "usr-123",
          "street": "999 Updated Street",
          "city": "Miami",
          "state": "FL",
          "country": "US",
          "postalCode": "33101",
          "additionalDetails": "Building A",
          "isDefault": true,
          "createdAt": "2026-01-15T10:30:00Z",
          "updatedAt": "2026-02-24T10:30:00Z"
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Validation Error", value = """
        {
          "success": false,
          "message": "State is required",
          "errorCode": "invalid_address",
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """))),
    @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Forbidden Error", value = """
        {
          "success": false,
          "message": "Access denied - ADMIN role required",
          "errorCode": "FORBIDDEN",
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Address not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Not Found Error", value = """
        {
          "success": false,
          "message": "Address not found with id: 999e4567-e89b-12d3-a456-426614174999",
          "errorCode": "NOT_FOUND",
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """)))
})
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UpdateAddressAdminAnnotation {
}
