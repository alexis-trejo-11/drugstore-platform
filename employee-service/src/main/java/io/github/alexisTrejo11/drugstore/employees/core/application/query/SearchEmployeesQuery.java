package io.github.alexisTrejo11.drugstore.employees.core.application.query;

import io.github.alexisTrejo11.drugstore.employees.core.domain.specification.EmployeeSearchCriteria;

/**
 * Query for searching employees with criteria
 */
public record SearchEmployeesQuery(EmployeeSearchCriteria criteria) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private EmployeeSearchCriteria criteria;

    public Builder criteria(EmployeeSearchCriteria criteria) {
      this.criteria = criteria;
      return this;
    }

    public SearchEmployeesQuery build() {
      return new SearchEmployeesQuery(criteria);
    }
  }
}
