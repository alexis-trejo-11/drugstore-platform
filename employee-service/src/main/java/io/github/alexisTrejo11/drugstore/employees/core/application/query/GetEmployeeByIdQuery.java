package io.github.alexisTrejo11.drugstore.employees.core.application.query;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Query for retrieving employee by ID
 */
public record GetEmployeeByIdQuery(EmployeeId employeeId) {
}
