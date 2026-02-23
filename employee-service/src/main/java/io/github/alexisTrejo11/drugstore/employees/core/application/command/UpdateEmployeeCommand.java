package io.github.alexisTrejo11.drugstore.employees.core.application.command;

import java.time.LocalDate;

import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.Address;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.ContactInfo;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;

/**
 * Command for updating employee personal information
 */
public record UpdateEmployeeCommand(
    EmployeeId employeeId,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    Address address,
    ContactInfo contactInfo,
    String updatedBy) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private EmployeeId employeeId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Address address;
    private ContactInfo contactInfo;
    private String updatedBy;

    public Builder employeeId(EmployeeId employeeId) {
      this.employeeId = employeeId;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder dateOfBirth(LocalDate dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
      return this;
    }

    public Builder address(Address address) {
      this.address = address;
      return this;
    }

    public Builder contactInfo(ContactInfo contactInfo) {
      this.contactInfo = contactInfo;
      return this;
    }

    public Builder updatedBy(String updatedBy) {
      this.updatedBy = updatedBy;
      return this;
    }

    public UpdateEmployeeCommand build() {
      return new UpdateEmployeeCommand(
          employeeId,
          firstName,
          lastName,
          dateOfBirth,
          address,
          contactInfo,
          updatedBy);
    }
  }
}
