package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Command for activating employee
 */
public record ActivateEmployeeCommand(
    EmployeeId employeeId,
    String reason,
    String activatedBy) {
}
