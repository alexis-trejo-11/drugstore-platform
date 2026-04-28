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

@Operation(summary = "Get my addresses", description = "Retrieves all active addresses for the authenticated user")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved addresses", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "success": true,
          "message": "Operation completed successfully",
          "data": [
            {
              "id": "123e4567-e89b-12d3-a456-426614174000",
              "street": "123 Main St",
              "city": "New York",
              "country": "US",
              "isDefault": true
            },
            {
              "id": "223e4567-e89b-12d3-a456-426614174001",
              "street": "456 Oak Avenue",
              "city": "Los Angeles",
              "country": "US",
              "isDefault": false
            }
          ],
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
        """)))
})
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetMyAddressesAnnotation {
}
