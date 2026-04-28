package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeRole;

/**
 * Command for changing employee role (promotion/demotion)
 */
public record ChangeRoleCommand(
    EmployeeId employeeId,
    EmployeeRole newRole,
    String reason,
    String approvedBy) {
}
