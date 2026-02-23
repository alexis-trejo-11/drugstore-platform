package io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject;

/**
 * Enum representing the status of an employee in the system
 */
public enum EmployeeStatus {
  /**
   * Employee is actively working
   */
  ACTIVE,

  /**
   * Employee is on approved leave (vacation, sick, maternity, etc.)
   */
  ON_LEAVE,

  /**
   * Employee is temporarily suspended (pending investigation, disciplinary
   * action,
   * etc.)
   */
  SUSPENDED,

  /**
   * Employee is inactive (not scheduled, waiting for assignment)
   */
  INACTIVE,

  /**
   * Employment has been terminated
   */
  TERMINATED;

  /**
   * Check if employee can work in this status
   */
  public boolean canWork() {
    return this == ACTIVE;
  }

  /**
   * Check if employee can be scheduled
   */
  public boolean canBeScheduled() {
    return this == ACTIVE || this == ON_LEAVE;
  }

  /**
   * Check if status is final (cannot be changed)
   */
  public boolean isFinal() {
    return this == TERMINATED;
  }

  /**
   * Check if employee can access systems
   */
  public boolean canAccessSystems() {
    return this == ACTIVE || this == ON_LEAVE;
  }
}
