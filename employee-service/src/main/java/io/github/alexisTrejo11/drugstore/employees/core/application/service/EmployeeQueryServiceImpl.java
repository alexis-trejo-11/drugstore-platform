package io.github.alexisTrejo11.drugstore.employees.core.application.service;

import java.util.Optional;

import io.github.alexisTrejo11.drugstore.employees.core.application.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexisTrejo11.drugstore.employees.core.port.input.EmployeeQueryService;
import io.github.alexisTrejo11.drugstore.employees.core.port.output.EmployeeRepository;
import io.github.alexisTrejo11.drugstore.employees.core.application.query.*;
import io.github.alexisTrejo11.drugstore.employees.core.domain.model.Employee;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of EmployeeQueryService
 * Handles all read operations
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EmployeeQueryServiceImpl implements EmployeeQueryService {

  private final EmployeeRepository employeeRepository;

  @Autowired
  public EmployeeQueryServiceImpl(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  public Optional<Employee> getEmployeeById(GetEmployeeByIdQuery query) {
    log.debug("Querying employee by ID: {}", query.employeeId());

    Optional<Employee> employee = employeeRepository.findById(query.employeeId());

    if (employee.isPresent()) {
      log.debug("Employee found: {}", query.employeeId());
    } else {
      log.debug("Employee not found: {}", query.employeeId());
    }

    return employee;
  }

  @Override
  public Optional<Employee> getEmployeeByNumber(GetEmployeeByNumberQuery query) {
    log.debug("Querying employee by number: {}", query.employeeNumber());

    Optional<Employee> employee = employeeRepository.findByEmployeeNumber(query.employeeNumber());

    if (employee.isPresent()) {
      log.debug("Employee found with number: {}", query.employeeNumber());
    } else {
      log.debug("Employee not found with number: {}", query.employeeNumber());
    }

    return employee;
  }

  @Override
  public Page<Employee> searchEmployees(SearchEmployeesQuery query) {
    log.debug("Searching employees with criteria: page={}, size={}",
        query.criteria().page(), query.criteria().size());

    Page<Employee> employees = employeeRepository.search(query.criteria());

    log.debug("Found {} employees (total: {})",
        employees.getNumberOfElements(), employees.getTotalElements());

    return employees;
  }

  @Override
  public long countEmployees(CountEmployeesQuery query) {
    log.debug("Counting employees with criteria");

    long count = employeeRepository.count(query.criteria());

    log.debug("Employee count: {}", count);
    return count;
  }

  @Override
  public boolean existsById(CheckEmployeeExistsByIdQuery query) {
    log.debug("Checking if employee exists by ID: {}", query.employeeId());

    boolean exists = employeeRepository.existsById(query.employeeId());

    log.debug("Employee exists by ID {}: {}", query.employeeId(), exists);
    return exists;
  }

  @Override
  public boolean existsByNumber(CheckEmployeeExistsByNumberQuery query) {
    log.debug("Checking if employee exists by number: {}", query.employeeNumber());

    boolean exists = employeeRepository.existsByEmployeeNumber(query.employeeNumber());

    log.debug("Employee exists by number {}: {}", query.employeeNumber(), exists);
    return exists;
  }
}
