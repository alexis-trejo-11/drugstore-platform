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

@Operation(summary = "Create address for any user", description = "Creates a new address for any user in the system (Admin only)")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Address created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "id": "123e4567-e89b-12d3-a456-426614174000",
          "userId": "usr-456",
          "street": "789 Broadway",
          "city": "Chicago",
          "state": "IL",
          "country": "US",
          "postalCode": "60601",
          "additionalDetails": "Floor 5",
          "isDefault": true,
          "createdAt": "2026-02-24T10:30:00Z",
          "updatedAt": "2026-02-24T10:30:00Z"
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Validation Error", value = """
        {
          "success": false,
          "message": "Postal code is required",
          "errorCode": "invalid_address",
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """))),
    @ApiResponse(responseCode = "403", description = "Access denied or address limit exceeded", content = @Content(mediaType = "application/json", examples = {
        @ExampleObject(name = "Access Denied", value = """
            {
              "success": false,
              "message": "Access denied - ADMIN role required",
              "errorCode": "FORBIDDEN",
              "timestamp": "2026-02-24T10:30:00Z"
            }
            """),
        @ExampleObject(name = "Limit Exceeded", value = """
            {
              "success": false,
              "message": "User usr-456 of type EMPLOYEE has reached the address limit of 1",
              "errorCode": "ADDRESS_LIMIT_EXCEEDED",
              "timestamp": "2026-02-24T10:30:00Z"
            }
            """)
    }))
})
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreateAddressForUserAnnotation {
}
