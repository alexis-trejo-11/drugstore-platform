package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.Certification;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Command for adding a certification to an employee
 */
public record AddCertificationCommand(
    EmployeeId employeeId,
    Certification certification,
    String addedBy) {
}
