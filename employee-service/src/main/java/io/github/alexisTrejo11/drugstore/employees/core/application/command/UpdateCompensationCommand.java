package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import java.math.BigDecimal;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Command for updating employee compensation
 */
public record UpdateCompensationCommand(
    EmployeeId employeeId,
    BigDecimal hourlyRate,
    Integer weeklyHours,
    String updatedBy) {
}
