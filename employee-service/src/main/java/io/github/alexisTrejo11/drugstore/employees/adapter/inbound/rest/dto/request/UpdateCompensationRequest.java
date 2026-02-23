package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.UpdateCompensationCommand;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Request DTO for updating employee compensation
 */
@Schema(description = "Update Compensation Request DTO")
public record UpdateCompensationRequest(
    @NotNull @DecimalMin(value = "0.01", message = "Hourly rate must be greater than zero") @Schema(description = "New hourly rate", example = "30.00", required = true) BigDecimal hourlyRate,

    @NotNull @Min(value = 1, message = "Weekly hours must be at least 1") @Schema(description = "New weekly hours", example = "40", required = true) Integer weeklyHours,

    @NotNull @NotBlank @Schema(description = "User performing the update", example = "admin@drugstore.com", required = true) String updatedBy) {

  public UpdateCompensationCommand toCommand(EmployeeId employeeId) {
    return new UpdateCompensationCommand(
        employeeId,
        this.hourlyRate,
        this.weeklyHours,
        this.updatedBy);
  }
}
