package io.github.alexisTrejo11.drugstore.employees.core.domain.specification;

import java.time.LocalDate;
import java.util.List;

/**
 * Search criteria for employee queries with pagination and filtering
 */
public record EmployeeSearchCriteria(
    String employeeNumber,
    String nameLike,
    String emailLike,
    String phoneLike,
    List<String> statuses,
    List<String> roles,
    List<String> employeeTypes,
    String storeId,
    String department,
    LocalDate hiredAfter,
    LocalDate hiredBefore,
    Boolean certificationExpiring,
    Integer certificationExpiringDays,
    int page,
    int size,
    SortOption sortBy) {

  public record SortOption(String field, String direction) {
    public static SortOption of(String field, String direction) {
      return new SortOption(field, direction);
    }

    public static SortOption byHireDate() {
      return new SortOption("hireDate", "DESC");
    }

    public static SortOption byLastName() {
      return new SortOption("lastName", "ASC");
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String employeeNumber;
    private String nameLike;
    private String emailLike;
    private String phoneLike;
    private List<String> statuses;
    private List<String> roles;
    private List<String> employeeTypes;
    private String storeId;
    private String department;
    private LocalDate hiredAfter;
    private LocalDate hiredBefore;
    private Boolean certificationExpiring;
    private Integer certificationExpiringDays;
    private int page = 0;
    private int size = 20;
    private SortOption sortBy;

    public Builder employeeNumber(String employeeNumber) {
      this.employeeNumber = employeeNumber;
      return this;
    }

    public Builder nameLike(String nameLike) {
      this.nameLike = nameLike;
      return this;
    }

    public Builder emailLike(String emailLike) {
      this.emailLike = emailLike;
      return this;
    }

    public Builder phoneLike(String phoneLike) {
      this.phoneLike = phoneLike;
      return this;
    }

    public Builder statuses(List<String> statuses) {
      this.statuses = statuses;
      return this;
    }

    public Builder roles(List<String> roles) {
      this.roles = roles;
      return this;
    }

    public Builder employeeTypes(List<String> employeeTypes) {
      this.employeeTypes = employeeTypes;
      return this;
    }

    public Builder storeId(String storeId) {
      this.storeId = storeId;
      return this;
    }

    public Builder department(String department) {
      this.department = department;
      return this;
    }

    public Builder hiredAfter(LocalDate hiredAfter) {
      this.hiredAfter = hiredAfter;
      return this;
    }

    public Builder hiredBefore(LocalDate hiredBefore) {
      this.hiredBefore = hiredBefore;
      return this;
    }

    public Builder certificationExpiring(Boolean certificationExpiring) {
      this.certificationExpiring = certificationExpiring;
      return this;
    }

    public Builder certificationExpiringDays(Integer certificationExpiringDays) {
      this.certificationExpiringDays = certificationExpiringDays;
      return this;
    }

    public Builder page(int page) {
      this.page = page;
      return this;
    }

    public Builder size(int size) {
      this.size = size;
      return this;
    }

    public Builder sortBy(SortOption sortBy) {
      this.sortBy = sortBy;
      return this;
    }

    public EmployeeSearchCriteria build() {
      return new EmployeeSearchCriteria(
          employeeNumber,
          nameLike,
          emailLike,
          phoneLike,
          statuses,
          roles,
          employeeTypes,
          storeId,
          department,
          hiredAfter,
          hiredBefore,
          certificationExpiring,
          certificationExpiringDays,
          page,
          size,
          sortBy);
    }
  }
}
