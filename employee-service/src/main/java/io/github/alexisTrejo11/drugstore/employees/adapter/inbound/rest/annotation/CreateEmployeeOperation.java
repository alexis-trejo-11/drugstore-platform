package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.annotation;

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
import libs_kernel.response.ResponseWrapper;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Create a new employee", description = """
    Creates a new employee in the system with complete information including:
    - Personal details (name, date of birth, contact info)
    - Employment details (role, type, department, store)
    - Compensation information (hourly rate, weekly hours)

    **Requires JWT authentication with ADMIN or HR role.**

    The employee will be created with ACTIVE status by default.
    """, tags = { "Employee Command Operations" })
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Employee created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Employee Creation Success", value = """
        {
          "isSuccess": true,
          "message": "Entity created successfully",
          "data": {
            "value": "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
          },
          "timestamp": "2026-02-22T14:25:30"
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Invalid request data or business rule violation", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Validation Error", value = """
        {
          "isSuccess": false,
          "message": "Employee number EMP-001 already exists",
          "timestamp": "2026-02-22T14:25:30"
        }
        """))),
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
})
public @interface CreateEmployeeOperation {
}
