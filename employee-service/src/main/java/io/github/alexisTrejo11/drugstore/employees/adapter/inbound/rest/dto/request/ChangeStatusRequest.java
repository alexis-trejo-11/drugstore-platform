package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.ChangeStatusCommand;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeStatus;

/**
 * Request DTO for changing employee status
 */
@Schema(description = "Change Status Request DTO")
public record ChangeStatusRequest(
    @NotNull @Schema(description = "New status", example = "ACTIVE", required = true) EmployeeStatus newStatus,

    @NotNull @NotBlank @Schema(description = "Reason for status change", example = "Returning from leave", required = true) String reason,

    @NotNull @NotBlank @Schema(description = "User performing the change", example = "hr@drugstore.com", required = true) String changedBy) {

  public ChangeStatusCommand toCommand(EmployeeId employeeId) {
    return new ChangeStatusCommand(
        employeeId,
        this.newStatus,
        this.reason,
        this.changedBy);
  }
}
