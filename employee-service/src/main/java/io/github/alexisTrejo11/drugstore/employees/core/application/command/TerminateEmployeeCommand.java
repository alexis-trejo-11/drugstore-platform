package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import java.time.LocalDate;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Command for terminating employee
 */
public record TerminateEmployeeCommand(
    EmployeeId employeeId,
    LocalDate terminationDate,
    String reason,
    String terminatedBy) {
}
