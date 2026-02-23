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
@Operation(summary = "Add certification to employee", description = """
    Adds a professional certification to an employee's profile.
    Validates that the certification is not expired.

    **Requires JWT authentication with ADMIN or HR role.**
    """, tags = { "Employee Command Operations" })
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Certification added successfully"),
    @ApiResponse(responseCode = "404", description = "Employee not found"),
    @ApiResponse(responseCode = "400", description = "Invalid certification data or expired"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden")
})
public @interface AddCertificationOperation {
}
