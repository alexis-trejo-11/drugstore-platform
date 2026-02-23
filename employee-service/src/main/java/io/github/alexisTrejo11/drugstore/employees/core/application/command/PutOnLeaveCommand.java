package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Command for putting employee on leave
 */
public record PutOnLeaveCommand(
    EmployeeId employeeId,
    String reason,
    String approvedBy) {
}
