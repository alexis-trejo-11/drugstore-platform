package io.github.alexisTrejo11.drugstore.employees.core.application.query;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeNumber;

/**
 * Query for retrieving employee by employee number
 */
public record GetEmployeeByNumberQuery(EmployeeNumber employeeNumber) {
}
