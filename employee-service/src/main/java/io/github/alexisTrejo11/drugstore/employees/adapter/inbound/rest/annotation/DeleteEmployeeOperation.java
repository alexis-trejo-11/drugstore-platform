package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Soft delete employee", description = """
    Soft deletes an employee from the system (logical deletion).
    The employee record is marked as deleted but remains in the database for audit purposes.

    **Requires JWT authentication with ADMIN role.**
    """, tags = { "Employee Command Operations" })
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Employee deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Employee not found"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden")
})
public @interface DeleteEmployeeOperation {
}
