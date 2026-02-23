package io.github.alexisTrejo11.drugstore.employees.core.application.query;

import io.github.alexisTrejo11.drugstore.employees.core.domain.specification.EmployeeSearchCriteria;

/**
 * Query for counting employees matching criteria
 */
public record CountEmployeesQuery(EmployeeSearchCriteria criteria) {
}
