package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeStatus;

/**
 * Command for changing employee status
 */
public record ChangeStatusCommand(
    EmployeeId employeeId,
    EmployeeStatus newStatus,
    String reason,
    String changedBy) {
}
