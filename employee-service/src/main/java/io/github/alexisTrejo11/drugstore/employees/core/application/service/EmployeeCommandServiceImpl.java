package io.github.alexisTrejo11.drugstore.employees.core.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexisTrejo11.drugstore.employees.core.application.command.ActivateEmployeeCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.AddCertificationCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.ChangeRoleCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.ChangeStatusCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.CreateEmployeeCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.DeleteEmployeeCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.PutOnLeaveCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.RestoreEmployeeCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.SuspendEmployeeCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.TerminateEmployeeCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.TransferEmployeeCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.UpdateCompensationCommand;
import io.github.alexisTrejo11.drugstore.employees.core.application.command.UpdateEmployeeCommand;
import io.github.alexisTrejo11.drugstore.employees.core.domain.exception.EmployeeNotFoundException;
import io.github.alexisTrejo11.drugstore.employees.core.domain.exception.InvalidEmployeeException;
import io.github.alexisTrejo11.drugstore.employees.core.domain.model.Employee;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;
import io.github.alexisTrejo11.drugstore.employees.core.port.input.EmployeeCommandService;
import io.github.alexisTrejo11.drugstore.employees.core.port.output.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of EmployeeCommandService
 * Handles all write operations with business logic
 */
@Service
@Slf4j
@Transactional
public class EmployeeCommandServiceImpl implements EmployeeCommandService {

  private final EmployeeRepository employeeRepository;

  @Autowired
  public EmployeeCommandServiceImpl(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  public EmployeeId createEmployee(CreateEmployeeCommand command) {
    log.info("Creating new employee: {} {}", command.firstName(), command.lastName());

    // Validate employee number uniqueness
    if (employeeRepository.existsByEmployeeNumber(command.employeeNumber())) {
      throw new InvalidEmployeeException(
          String.format("Employee number %s already exists", command.employeeNumber().getValue()));
    }

    // Create employee using factory method (contains business logic)
    Employee employee = Employee.hire(
        command.employeeNumber(),
        command.firstName(),
        command.lastName(),
        command.dateOfBirth(),
        command.contactInfo(),
        command.role(),
        command.employeeType(),
        command.department(),
        command.storeId(),
        command.hireDate(),
        command.hourlyRate(),
        command.weeklyHours(),
        command.createdBy());

    // Set workday schedule if provided
    if (command.workdaySchedule() != null) {
      employee.setWorkdaySchedule(command.workdaySchedule());
    }

    // Persist employee
    Employee savedEmployee = employeeRepository.save(employee);

    log.info("Employee created successfully with ID: {}", savedEmployee.getId());
    return savedEmployee.getId();
  }

  @Override
  public void updateEmployee(UpdateEmployeeCommand command) {
    log.info("Updating employee: {}", command.employeeId());

    // Retrieve employee
    Employee employee = employeeRepository.findById(command.employeeId())
        .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId().getValue()));

    // Update personal information
    if (command.firstName() != null) {
      employee.setFirstName(command.firstName());
    }
    if (command.lastName() != null) {
      employee.setLastName(command.lastName());
    }
    if (command.dateOfBirth() != null) {
      employee.setDateOfBirth(command.dateOfBirth());
    }
    if (command.contactInfo() != null) {
      employee.setContactInfo(command.contactInfo());
    }
    if (command.workdaySchedule() != null) {
      employee.setWorkdaySchedule(command.workdaySchedule());
    }

    employee.setLastModifiedBy(command.updatedBy());

    // Persist changes
    employeeRepository.save(employee);

    log.info("Employee updated successfully: {}", command.employeeId());
  }

  @Override
  public void addCertification(AddCertificationCommand command) {
    log.info("Adding certification to employee: {}", command.employeeId());

    // Retrieve employee
    Employee employee = employeeRepository.findById(command.employeeId())
        .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId().getValue()));

    // Add certification using business method (contains validation)
    employee.addCertification(command.certification());
    employee.setLastModifiedBy(command.addedBy());

    // Persist changes
    employeeRepository.save(employee);

    log.info("Certification added successfully to employee: {}", command.employeeId());
  }

  @Override
  public void changeRole(ChangeRoleCommand command) {
    log.info("Changing role for employee: {}", command.employeeId());

    // Retrieve employee
    Employee employee = employeeRepository.findById(command.employeeId())
        .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId().getValue()));

    // Change role using business method (contains validation and events)
    employee.changeRole(command.newRole(), command.reason(), command.approvedBy());

    // Persist changes
    employeeRepository.save(employee);

    log.info("Role changed successfully for employee: {}", command.employeeId());
  }

  @Override
  public void changeStatus(ChangeStatusCommand command) {
    log.info("Changing status for employee: {}", command.employeeId());

    // Retrieve employee
    Employee employee = employeeRepository.findById(command.employeeId())
        .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId().getValue()));

    // Change status using business method (contains validation and events)
    employee.changeStatus(command.newStatus(), command.reason(), command.changedBy());

    // Persist changes
    employeeRepository.save(employee);

    log.info("Status changed successfully for employee: {}", command.employeeId());
  }

  @Override
  public void updateCompensation(UpdateCompensationCommand command) {
    log.info("Updating compensation for employee: {}", command.employeeId());

    // Retrieve employee
    Employee employee = employeeRepository.findById(command.employeeId())
        .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId().getValue()));

    // Update compensation using business method (contains validation)
    employee.updateCompensation(command.hourlyRate(), command.weeklyHours(), command.updatedBy());

    // Persist changes
    employeeRepository.save(employee);

    log.info("Compensation updated successfully for employee: {}", command.employeeId());
  }

  @Override
  public void transferEmployee(TransferEmployeeCommand command) {
    log.info("Transferring employee: {}", command.employeeId());

    // Retrieve employee
    Employee employee = employeeRepository.findById(command.employeeId())
        .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId().getValue()));

    // Transfer using business method (contains validation)
    employee.transfer(command.newStoreId(), command.newDepartment(), command.approvedBy());

    // Persist changes
    employeeRepository.save(employee);

    log.info("Employee transferred successfully: {}", command.employeeId());
  }

  @Override
  public void terminateEmployee(TerminateEmployeeCommand command) {
    log.info("Terminating employee: {}", command.employeeId());

    // Retrieve employee
    Employee employee = employeeRepository.findById(command.employeeId())
        .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId().getValue()));

    // Terminate using business method (contains validation and events)
    employee.terminate(command.terminationDate(), command.reason(), command.terminatedBy());

    // Persist changes
    employeeRepository.save(employee);

    log.info("Employee terminated successfully: {}", command.employeeId());
  }

  @Override
  public void suspendEmployee(SuspendEmployeeCommand command) {
    log.info("Suspending employee: {}", command.employeeId());

    // Retrieve employee
    Employee employee = employeeRepository.findById(command.employeeId())
        .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId().getValue()));

    // Suspend using business method (contains validation and events)
    employee.suspend(command.reason(), command.suspendedBy());

    // Persist changes
    employeeRepository.save(employee);

    log.info("Employee suspended successfully: {}", command.employeeId());
  }

  @Override
  public void activateEmployee(ActivateEmployeeCommand command) {
    log.info("Activating employee: {}", command.employeeId());

    // Retrieve employee
    Employee employee = employeeRepository.findById(command.employeeId())
        .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId().getValue()));

    // Activate using business method (contains validation and events)
    employee.activate(command.reason(), command.activatedBy());

    // Persist changes
    employeeRepository.save(employee);

    log.info("Employee activated successfully: {}", command.employeeId());
  }

  @Override
  public void putOnLeave(PutOnLeaveCommand command) {
    log.info("Putting employee on leave: {}", command.employeeId());

    // Retrieve employee
    Employee employee = employeeRepository.findById(command.employeeId())
        .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId().getValue()));

    // Put on leave using business method (contains validation and events)
    employee.putOnLeave(command.reason(), command.approvedBy());

    // Persist changes
    employeeRepository.save(employee);

    log.info("Employee put on leave successfully: {}", command.employeeId());
  }

  @Override
  public void deleteEmployee(DeleteEmployeeCommand command) {
    log.info("Soft deleting employee: {}", command.employeeId());

    // Verify employee exists
    if (!employeeRepository.existsById(command.employeeId())) {
      throw new EmployeeNotFoundException(command.employeeId().getValue());
    }

    // Soft delete using repository
    employeeRepository.deleteById(command.employeeId());

    log.info("Employee soft deleted successfully: {}", command.employeeId());
  }

  @Override
  public void restoreEmployee(RestoreEmployeeCommand command) {
    log.info("Restoring employee: {}", command.employeeId());

    // Restore using repository
    employeeRepository.restoreById(command.employeeId());

    log.info("Employee restored successfully: {}", command.employeeId());
  }
}
