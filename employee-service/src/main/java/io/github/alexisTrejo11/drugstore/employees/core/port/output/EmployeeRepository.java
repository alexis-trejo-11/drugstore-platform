package io.github.alexisTrejo11.drugstore.employees.core.port.output;

import java.util.Optional;

import org.springframework.data.domain.Page;

import io.github.alexisTrejo11.drugstore.employees.core.domain.model.Employee;
import io.github.alexisTrejo11.drugstore.employees.core.domain.specification.EmployeeSearchCriteria;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeNumber;

/**
 * Output port for Employee repository operations
 * Defines the contract for persistence operations
 */
public interface EmployeeRepository {

  /**
   * Save an employee (create or update)
   */
  Employee save(Employee employee);

  /**
   * Find employee by ID
   */
  Optional<Employee> findById(EmployeeId id);

  /**
   * Find employee by employee number
   */
  Optional<Employee> findByEmployeeNumber(EmployeeNumber employeeNumber);

  /**
   * Search employees with criteria and pagination
   */
  Page<Employee> search(EmployeeSearchCriteria criteria);

  /**
   * Count employees matching criteria
   */
  long count(EmployeeSearchCriteria criteria);

  /**
   * Check if employee exists by ID
   */
  boolean existsById(EmployeeId id);

  /**
   * Check if employee exists by employee number
   */
  boolean existsByEmployeeNumber(EmployeeNumber employeeNumber);

  /**
   * Soft delete employee by ID
   */
  void deleteById(EmployeeId id);

  /**
   * Restore a soft-deleted employee
   */
  void restoreById(EmployeeId id);
}
