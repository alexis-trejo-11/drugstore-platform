package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.request;

import java.time.LocalDate;
import java.util.Map;

import io.github.alexisTrejo11.drugstore.employees.core.application.command.UpdateEmployeeCommand;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

/**
 * Request DTO for updating employee information
 */
@Schema(description = "Update Employee Request DTO")
public record UpdateEmployeeRequest(
    @NotNull @Schema(description = "First name", example = "John", required = true) String firstName,

    @NotNull @Schema(description = "Last name", example = "Doe", required = true) String lastName,

    @NotNull @Past @Schema(description = "Date of birth", example = "1990-05-15", required = true) LocalDate dateOfBirth,

    @NotNull @Valid @Schema(description = "Contact information", required = true) ContactInfoRequest contactInfo,

    @Schema(description = "Workday schedule (JSON)") Map<String, Object> workdaySchedule,

    @NotNull @Schema(description = "User performing the update", example = "admin@drugstore.com", required = true) String updatedBy) {

  public UpdateEmployeeCommand toCommand(EmployeeId employeeId) {
    return UpdateEmployeeCommand.builder()
        .employeeId(employeeId)
        .firstName(this.firstName)
        .lastName(this.lastName)
        .dateOfBirth(this.dateOfBirth)
        .contactInfo(this.contactInfo.toDomain())
        .workdaySchedule(this.workdaySchedule)
        .updatedBy(this.updatedBy)
        .build();
  }
}
