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
@Operation(summary = "Get users by role (paginated)", description = "Retrieves all users with a specific role with pagination support. Available roles: CUSTOMER, EMPLOYEE, ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Success with pagination", value = """
        {
          "message": "Users retrieved successfully",
          "data": {
            "content": [
              {
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "email": "john.doe@example.com",
                "phoneNumber": "+1-555-123-4567",
                "role": "CUSTOMER",
                "joinedAt": "2026-01-15T10:30:00",
                "lastLoginAt": "2026-02-19T09:15:00"
              },
              {
                "id": "660e8400-e29b-41d4-a716-446655440001",
                "email": "jane.smith@example.com",
                "phoneNumber": "+1-555-987-6543",
                "role": "CUSTOMER",
                "joinedAt": "2026-01-20T14:22:00",
                "lastLoginAt": "2026-02-18T16:45:00"
              }
            ],
            "pagination_metadata": {
              "totalElements": 150,
              "totalPages": 15,
              "currentPage": 0,
              "pageSize": 10,
              "hasNext": true,
              "hasPrevious": false
            }
          },
          "timestamp": "2026-02-19T10:30:00"
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid role or pagination parameters", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "Invalid role",
          "error": {
            "code": "INVALID_ROLE",
            "details": "Role must be one of: CUSTOMER, EMPLOYEE, ADMIN"
          },
          "timestamp": "2026-02-19T10:30:00"
        }
        """))),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
})
public @interface GetUsersByRoleOperation {

}
