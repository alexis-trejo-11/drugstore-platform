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
@Operation(summary = "Get employee by ID", description = """
    Retrieves detailed information about a specific employee by their unique identifier.

    **Requires JWT authentication.**
    """, tags = { "Employee Query Operations" })
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Employee found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
    @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "isSuccess": false,
          "message": "Employee not found with ID: c1a2b3d4-e5f6-7890",
          "timestamp": "2026-02-22T14:25:30"
        }
        """))),
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
})
public @interface GetEmployeeByIdOperation {
}
