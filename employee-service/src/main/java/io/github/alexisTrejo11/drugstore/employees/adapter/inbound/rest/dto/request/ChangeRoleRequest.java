package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.ChangeRoleCommand;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeRole;

/**
 * Request DTO for changing employee role
 */
@Schema(description = "Change Role Request DTO")
public record ChangeRoleRequest(
    @NotNull @Schema(description = "New role", example = "SENIOR_PHARMACIST", required = true) EmployeeRole newRole,

    @NotNull @NotBlank @Schema(description = "Reason for role change", example = "Promotion due to performance", required = true) String reason,

    @NotNull @NotBlank @Schema(description = "User approving the change", example = "hr@drugstore.com", required = true) String approvedBy) {

  public ChangeRoleCommand toCommand(EmployeeId employeeId) {
    return new ChangeRoleCommand(
        employeeId,
        this.newRole,
        this.reason,
        this.approvedBy);
  }
}
