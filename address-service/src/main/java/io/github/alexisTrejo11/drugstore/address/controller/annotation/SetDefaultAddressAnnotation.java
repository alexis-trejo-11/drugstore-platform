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

@Operation(summary = "Set address as default", description = "Sets a specific address as the default address for the authenticated user. Only one address can be default at a time.")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Address set as default successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "success": true,
          "message": "Operation completed successfully",
          "data": {
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
            "updatedAt": "2026-02-24T10:30:00Z"
          },
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """))),
    @ApiResponse(responseCode = "403", description = "Access denied - Address belongs to another user", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Forbidden Error", value = """
        {
          "success": false,
          "message": "User usr-456 is not authorized to access address 123e4567-e89b-12d3-a456-426614174000",
          "errorCode": "ADDRESS_ACCESS_UNAUTHORIZED",
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Address not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Not Found Error", value = """
        {
          "success": false,
          "message": "Address not found with id: 999e4567-e89b-12d3-a456-426614174999 for user usr-123",
          "errorCode": "NOT_FOUND",
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """)))
})
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SetDefaultAddressAnnotation {
}
