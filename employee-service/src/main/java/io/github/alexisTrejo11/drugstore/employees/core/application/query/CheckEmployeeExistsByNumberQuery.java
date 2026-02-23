package io.github.alexisTrejo11.drugstore.employees.core.application.query;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeNumber;

/**
 * Query for checking if employee exists by employee number
 */
public record CheckEmployeeExistsByNumberQuery(EmployeeNumber employeeNumber) {
}
