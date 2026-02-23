package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Command for soft deleting employee
 */
public record DeleteEmployeeCommand(
    EmployeeId employeeId,
    String deletedBy) {
}
