package io.github.alexisTrejo11.drugstore.employees.core.port.input;

import io.github.alexisTrejo11.drugstore.employees.core.application.command.*;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Input port for employee command operations (write operations)
 * Follows CQRS pattern - separates write operations from reads
 */
public interface EmployeeCommandService {

  /**
   * Create a new employee
   * 
   * @param command Create employee command
   * @return EmployeeId of the created employee
   */
  EmployeeId createEmployee(CreateEmployeeCommand command);

  /**
   * Update employee personal information
   * 
   * @param command Update employee command
   */
  void updateEmployee(UpdateEmployeeCommand command);

  /**
   * Add certification to employee
   * 
   * @param command Add certification command
   */
  void addCertification(AddCertificationCommand command);

  /**
   * Change employee role (promotion/demotion)
   * 
   * @param command Change role command
   */
  void changeRole(ChangeRoleCommand command);

  /**
   * Change employee status
   * 
   * @param command Change status command
   */
  void changeStatus(ChangeStatusCommand command);

  /**
   * Update employee compensation
   * 
   * @param command Update compensation command
   */
  void updateCompensation(UpdateCompensationCommand command);

  /**
   * Transfer employee to another store/department
   * 
   * @param command Transfer employee command
   */
  void transferEmployee(TransferEmployeeCommand command);

  /**
   * Terminate employee
   * 
   * @param command Terminate employee command
   */
  void terminateEmployee(TerminateEmployeeCommand command);

  /**
   * Suspend employee
   * 
   * @param command Suspend employee command
   */
  void suspendEmployee(SuspendEmployeeCommand command);

  /**
   * Activate employee
   * 
   * @param command Activate employee command
   */
  void activateEmployee(ActivateEmployeeCommand command);

  /**
   * Put employee on leave
   * 
   * @param command Put on leave command
   */
  void putOnLeave(PutOnLeaveCommand command);

  /**
   * Soft delete employee
   * 
   * @param command Delete employee command
   */
  void deleteEmployee(DeleteEmployeeCommand command);

  /**
   * Restore soft-deleted employee
   * 
   * @param command Restore employee command
   */
  void restoreEmployee(RestoreEmployeeCommand command);
}
