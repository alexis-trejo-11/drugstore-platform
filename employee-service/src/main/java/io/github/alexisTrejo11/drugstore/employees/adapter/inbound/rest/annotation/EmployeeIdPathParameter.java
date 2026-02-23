package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(name = "id", description = "Unique employee identifier (UUID format)", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string", format = "uuid", example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"))
public @interface EmployeeIdPathParameter {
}
