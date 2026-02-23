package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.github.alexisTrejo11.drugstore.employees.core.application.query.SearchEmployeesQuery;
import io.github.alexisTrejo11.drugstore.employees.core.domain.specification.EmployeeSearchCriteria;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeRole;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeStatus;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Request DTO for searching employees
 */
@Schema(description = "Search Employees Request DTO")
public record SearchEmployeesRequest(
    @Schema(description = "Employee number to search", example = "EMP-001") String employeeNumber,

    @Schema(description = "Name to search (first or last name)", example = "John") String nameLike,

    @Schema(description = "Email to search", example = "john") String emailLike,

    @Schema(description = "Phone to search", example = "555") String phoneLike,

    @Schema(description = "Employee statuses to filter", example = "[\"ACTIVE\", \"ON_LEAVE\"]") Set<EmployeeStatus> statuses,

    @Schema(description = "Employee roles to filter", example = "[\"PHARMACIST\"]") Set<EmployeeRole> roles,

    @Schema(description = "Employee types to filter", example = "[\"FULL_TIME\"]") Set<EmployeeType> employeeTypes,

    @Schema(description = "Store ID to filter", example = "store-123") String storeId,

    @Schema(description = "Department to filter", example = "Pharmacy") String department,

    @Schema(description = "Hired after date", example = "2023-01-01") LocalDate hiredAfter,

    @Schema(description = "Hired before date", example = "2024-12-31") LocalDate hiredBefore,

    @Schema(description = "Days threshold for certifications expiring soon", example = "30") Integer certificationExpiringInDays,

    @Schema(description = "Include soft-deleted employees", example = "false") Boolean includeDeleted,

    @Schema(description = "Page number (0-based)", example = "0") Integer page,

    @Schema(description = "Page size", example = "20") Integer size,

    @Schema(description = "Sort field", example = "lastName") String sortField,

    @Schema(description = "Sort direction", example = "ASC") String sortDirection) {

  public SearchEmployeesQuery toQuery() {
    int pageNumber = page != null && page >= 0 ? page : 0;
    int pageSize = size != null && size > 0 ? size : 20;

    EmployeeSearchCriteria.SortOption sortOption = null;
    if (sortField != null) {
      String direction = sortDirection != null ? sortDirection.toUpperCase() : "ASC";
      sortOption = new EmployeeSearchCriteria.SortOption(sortField, direction);
    }

		List<String> statusList = this.statuses != null ? new ArrayList<>(this.statuses.stream().map(EmployeeStatus::name).toList()) : null;
		List<String> roleList = this.roles != null ? new ArrayList<>(this.roles.stream().map(EmployeeRole::name).toList()) : null;
		List<String> typeList = this.employeeTypes != null ? new ArrayList<>(this.employeeTypes.stream().map(EmployeeType::name).toList()) : null;

    EmployeeSearchCriteria criteria = EmployeeSearchCriteria.builder()
        .employeeNumber(employeeNumber)
        .nameLike(nameLike)
        .emailLike(emailLike)
        .phoneLike(phoneLike)
        .statuses(statusList)
        .roles(roleList)
        .employeeTypes(typeList)
        .storeId(storeId)
        .department(department)
        .hiredAfter(hiredAfter)
        .hiredBefore(hiredBefore)
        .certificationExpiringDays(certificationExpiringInDays)
        //.includeDeleted(includeDeleted != null ? includeDeleted : false)
        .page(pageNumber)
        .size(pageSize)
        .sortBy(sortOption)
        .build();

    return new SearchEmployeesQuery(criteria);
  }
}
