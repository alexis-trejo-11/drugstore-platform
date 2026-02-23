package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import libs_kernel.response.ResponseWrapper;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Search employees", description = """
    Search employees with multiple filter criteria including:
    - Employee number, name, email, phone
    - Status, role, employment type
    - Store, department
    - Hire date range
    - Certifications expiring soon

    Supports pagination and sorting.

    **Requires JWT authentication.**
    """, tags = { "Employee Query Operations" })
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Employees found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
})
public @interface SearchEmployeesOperation {
}
