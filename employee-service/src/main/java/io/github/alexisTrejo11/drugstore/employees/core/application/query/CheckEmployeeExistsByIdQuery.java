package io.github.alexisTrejo11.drugstore.employees.core.application.query;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Query for checking if employee exists by ID
 */
public record CheckEmployeeExistsByIdQuery(EmployeeId employeeId) {
}
