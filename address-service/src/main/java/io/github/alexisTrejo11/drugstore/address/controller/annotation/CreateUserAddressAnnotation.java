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

@Operation(summary = "Create new address", description = "Creates a new address for the authenticated user. Customers can have up to 5 addresses, employees up to 1.")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Address created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "success": true,
          "message": "Address created successfully",
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
            "createdAt": "2026-02-24T10:30:00Z",
            "updatedAt": "2026-02-24T10:30:00Z"
          },
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Validation Error", value = """
        {
          "success": false,
          "message": "City is required",
          "errorCode": "invalid_address",
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """))),
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Unauthorized Error", value = """
        {
          "success": false,
          "message": "Authentication required",
          "errorCode": "UNAUTHORIZED",
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """))),
    @ApiResponse(responseCode = "403", description = "Address limit exceeded", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Limit Exceeded Error", value = """
        {
          "success": false,
          "message": "User usr-123 of type CUSTOMER has reached the address limit of 5",
          "errorCode": "ADDRESS_LIMIT_EXCEEDED",
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """))),
    @ApiResponse(responseCode = "422", description = "Business rule violation", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Business Rule Error", value = """
        {
          "success": false,
          "message": "Invalid postal code format for country: US",
          "errorCode": "invalid_address",
          "timestamp": "2026-02-24T10:30:00Z"
        }
        """)))
})
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreateUserAddressAnnotation {
}
