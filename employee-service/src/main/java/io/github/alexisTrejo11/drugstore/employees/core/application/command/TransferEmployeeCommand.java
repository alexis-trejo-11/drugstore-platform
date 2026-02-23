package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Command for transferring employee to another store/department
 */
public record TransferEmployeeCommand(
    EmployeeId employeeId,
    String newStoreId,
    String newDepartment,
    String approvedBy) {
}
