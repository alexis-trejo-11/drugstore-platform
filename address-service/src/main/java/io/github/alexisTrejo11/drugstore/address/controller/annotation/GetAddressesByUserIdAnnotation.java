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

@Operation(summary = "Get addresses by user ID", description = "Retrieves all addresses for a specific user (Admin only)")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user addresses", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class), examples = @ExampleObject(name = "Success Response", value = """
        [
          {
            "id": "123e4567-e89b-12d3-a456-426614174000",
            "userId": "usr-123",
            "street": "123 Main St",
            "city": "New York",
            "state": "NY",
            "country": "US",
            "postalCode": "10001",
            "additionalDetails": "Apt 4B",
            "isDefault": true,
            "createdAt": "2026-01-15T10:30:00Z",
            "updatedAt": "2026-02-20T14:20:00Z"
          },
          {
            "id": "223e4567-e89b-12d3-a456-426614174001",
            "userId": "usr-123",
            "street": "456 Oak Avenue",
            "city": "Los Angeles",
            "state": "CA",
            "country": "US",
            "postalCode": "90001",
            "additionalDetails": null,
            "isDefault": false,
            "createdAt": "2026-02-10T08:15:00Z",
            "updatedAt": "2026-02-10T08:15:00Z"
          }
        ]
        """))),
    @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Forbidden Error", value = """
        {
          "success": false,
          "message": "Access denied - ADMIN role required",
          "errorCode": "FORBIDDEN",
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """)))
})
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetAddressesByUserIdAnnotation {
}
