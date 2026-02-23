package io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject;

/**
 * Enum representing the role/position of an employee in the drugstore
 */
public enum EmployeeRole {
  /**
   * Licensed pharmacist - can dispense medication and provide consultation
   */
  PHARMACIST("Pharmacist", true, 4),

  /**
   * Pharmacy technician - assists pharmacist but cannot dispense controlled
   * substances alone
   */
  PHARMACY_TECHNICIAN("Pharmacy Technician", true, 3),

  /**
   * Store manager - responsible for overall store operations
   */
  STORE_MANAGER("Store Manager", false, 5),

  /**
   * Assistant store manager
   */
  ASSISTANT_MANAGER("Assistant Manager", false, 4),

  /**
   * Inventory specialist - manages stock and ordering
   */
  INVENTORY_SPECIALIST("Inventory Specialist", false, 3),

  /**
   * Cashier - handles customer transactions
   */
  CASHIER("Cashier", false, 2),

  /**
   * Sales associate - assists customers, stocks shelves
   */
  SALES_ASSOCIATE("Sales Associate", false, 2),

  /**
   * Warehouse staff - handles receiving and storage
   */
  WAREHOUSE_STAFF("Warehouse Staff", false, 2),

  /**
   * Security personnel
   */
  SECURITY("Security", false, 2),

  /**
   * Janitorial staff
   */
  JANITORIAL("Janitorial Staff", false, 1);

  private final String displayName;
  private final boolean requiresCertification;
  private final int accessLevel;

  EmployeeRole(String displayName, boolean requiresCertification, int accessLevel) {
    this.displayName = displayName;
    this.requiresCertification = requiresCertification;
    this.accessLevel = accessLevel;
  }

  public String getDisplayName() {
    return displayName;
  }

  public boolean requiresCertification() {
    return requiresCertification;
  }

  public int getAccessLevel() {
    return accessLevel;
  }

  /**
   * Check if this role can supervise another role
   */
  public boolean canSupervise(EmployeeRole otherRole) {
    return this.accessLevel > otherRole.accessLevel;
  }

  /**
   * Check if this role can dispense medication
   */
  public boolean canDispenseMedication() {
    return this == PHARMACIST;
  }

  /**
   * Check if this role handles controlled substances
   */
  public boolean handlesControlledSubstances() {
    return this == PHARMACIST || this == PHARMACY_TECHNICIAN;
  }
}
