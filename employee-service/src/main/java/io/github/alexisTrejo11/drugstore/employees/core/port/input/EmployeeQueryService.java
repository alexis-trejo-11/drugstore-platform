package io.github.alexisTrejo11.drugstore.employees.core.port.input;

import java.util.Optional;

import io.github.alexisTrejo11.drugstore.employees.core.application.query.*;
import org.springframework.data.domain.Page;

import io.github.alexisTrejo11.drugstore.employees.core.application.query.*;
import io.github.alexisTrejo11.drugstore.employees.core.domain.model.Employee;

/**
 * Input port for employee query operations (read operations)
 * Follows CQRS pattern - separates read operations from writes
 */
public interface EmployeeQueryService {

  /**
   * Get employee by ID
   * 
   * @param query Get employee by ID query
   * @return Optional containing the employee if found
   */
  Optional<Employee> getEmployeeById(GetEmployeeByIdQuery query);

  /**
   * Get employee by employee number
   * 
   * @param query Get employee by number query
   * @return Optional containing the employee if found
   */
  Optional<Employee> getEmployeeByNumber(GetEmployeeByNumberQuery query);

  /**
   * Search employees with criteria
   * 
   * @param query Search employees query
   * @return Page of employees matching the criteria
   */
  Page<Employee> searchEmployees(SearchEmployeesQuery query);

  /**
   * Count employees matching criteria
   * 
   * @param query Count employees query
   * @return Number of employees matching the criteria
   */
  long countEmployees(CountEmployeesQuery query);

  /**
   * Check if employee exists by ID
   * 
   * @param query Check employee exists by ID query
   * @return true if employee exists, false otherwise
   */
  boolean existsById(CheckEmployeeExistsByIdQuery query);

  /**
   * Check if employee exists by employee number
   * 
   * @param query Check employee exists by number query
   * @return true if employee exists, false otherwise
   */
  boolean existsByNumber(CheckEmployeeExistsByNumberQuery query);
}
