package io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject;

/**
 * Enum representing the employment type
 */
public enum EmployeeType {
  /**
   * Full-time employee (40+ hours/week)
   */
  FULL_TIME(40, null),

  /**
   * Part-time employee (less than 40 hours/week)
   */
  PART_TIME(20, 39),

  /**
   * Contract employee (fixed-term contract)
   */
  CONTRACT(40, null),

  /**
   * Temporary/seasonal employee
   */
  TEMPORARY(30, 40),

  /**
   * Intern
   */
  INTERN(20, 30);

  private final int minWeeklyHours;
  private final Integer maxWeeklyHours;

  EmployeeType(int minWeeklyHours, Integer maxWeeklyHours) {
    this.minWeeklyHours = minWeeklyHours;
    this.maxWeeklyHours = maxWeeklyHours;
  }

  public int getMinWeeklyHours() {
    return minWeeklyHours;
  }

  public Integer getMaxWeeklyHours() {
    return maxWeeklyHours;
  }

  /**
   * Check if the given weekly hours are valid for this employment type
   */
  public boolean isValidWeeklyHours(int hours) {
    if (hours < minWeeklyHours) {
      return false;
    }
    return maxWeeklyHours == null || hours <= maxWeeklyHours;
  }

  /**
   * Check if employee is eligible for benefits
   */
  public boolean isEligibleForBenefits() {
    return this == FULL_TIME || this == CONTRACT;
  }
}
