package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Command for suspending employee
 */
public record SuspendEmployeeCommand(
    EmployeeId employeeId,
    String reason,
    String suspendedBy) {
}
