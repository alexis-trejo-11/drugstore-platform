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

@Operation(summary = "Get all addresses with pagination", description = "Retrieves a paginated list of all active addresses in the system (Admin only)")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved addresses", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "content": [
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
          "pageable": {
            "pageNumber": 0,
            "pageSize": 20,
            "sort": {
              "sorted": false,
              "unsorted": true,
              "empty": true
            }
          },
          "totalElements": 150,
          "totalPages": 8,
          "last": false,
          "first": true,
          "size": 20,
          "number": 0
        }
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
public @interface GetAllAddressesAnnotation {
}
