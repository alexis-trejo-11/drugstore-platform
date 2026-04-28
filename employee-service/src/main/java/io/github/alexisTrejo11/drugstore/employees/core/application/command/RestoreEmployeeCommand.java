package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Command for restoring soft-deleted employee
 */
public record RestoreEmployeeCommand(
    EmployeeId employeeId,
    String restoredBy) {
}
