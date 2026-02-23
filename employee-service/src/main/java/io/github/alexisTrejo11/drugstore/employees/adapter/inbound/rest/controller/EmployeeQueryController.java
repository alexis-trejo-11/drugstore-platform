package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.controller;

import java.util.Optional;

import io.github.alexisTrejo11.drugstore.employees.core.application.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import io.github.alexisTrejo11.drugstore.employees.core.port.input.EmployeeQueryService;
import io.github.alexisTrejo11.drugstore.employees.core.domain.exception.EmployeeNotFoundException;
import io.github.alexisTrejo11.drugstore.employees.core.domain.model.Employee;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeNumber;
import io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.annotation.*;
import io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.request.SearchEmployeesRequest;
import io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.response.EmployeeResponse;
import io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.mapper.EmployeeResponseMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;

/**
 * REST Controller for employee query operations (read operations)
 */
@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Query Operations", description = "Endpoints for querying and retrieving employee information. These operations allow searching, filtering, and retrieving employee data based on various criteria.")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeQueryController {

  private final EmployeeQueryService employeeQueryService;
  private final EmployeeResponseMapper responseMapper;

  @Autowired
  public EmployeeQueryController(
      EmployeeQueryService employeeQueryService,
      EmployeeResponseMapper responseMapper) {
    this.employeeQueryService = employeeQueryService;
    this.responseMapper = responseMapper;
  }

  @GetMapping("/{id}")
  @RateLimit(profile = RateLimitProfile.PUBLIC)
  @GetEmployeeByIdOperation
  public ResponseWrapper<EmployeeResponse> getEmployeeById(
      @EmployeeIdPathParameter @PathVariable String id) {

    GetEmployeeByIdQuery query = new GetEmployeeByIdQuery(EmployeeId.of(id));
    Optional<Employee> employee = employeeQueryService.getEmployeeById(query);

    if (employee.isEmpty()) {
      throw new EmployeeNotFoundException(id);
    }

    EmployeeResponse response = responseMapper.toResponse(employee.get());
    return ResponseWrapper.found(response, "Employee");
  }

  @GetMapping("/by-number/{employeeNumber}")
  @RateLimit(profile = RateLimitProfile.PUBLIC)
  public ResponseWrapper<EmployeeResponse> getEmployeeByNumber(
      @Parameter(description = "Unique employee number", required = true, example = "EMP-001") @PathVariable String employeeNumber) {

    GetEmployeeByNumberQuery query = new GetEmployeeByNumberQuery(
        EmployeeNumber.of(employeeNumber));
    Optional<Employee> employee = employeeQueryService.getEmployeeByNumber(query);

    if (employee.isEmpty()) {
      throw new EmployeeNotFoundException("Employee not found with number: " + employeeNumber);
    }

    EmployeeResponse response = responseMapper.toResponse(employee.get());
    return ResponseWrapper.found(response, "Employee");
  }

  @GetMapping
  @RateLimit(profile = RateLimitProfile.PUBLIC)
  @SearchEmployeesOperation
  public ResponseWrapper<PageResponse<EmployeeResponse>> searchEmployees(
      @ModelAttribute SearchEmployeesRequest request) {

    SearchEmployeesQuery query = request.toQuery();
    Page<Employee> employeesPage = employeeQueryService.searchEmployees(query);

    PageResponse<EmployeeResponse> pageResponse = responseMapper.toResponsePage(employeesPage);
    return ResponseWrapper.found(pageResponse, "Employees");
  }

  @GetMapping("/exists/id/{id}")
  @RateLimit(profile = RateLimitProfile.PUBLIC)
  public ResponseWrapper<Boolean> existsById(
      @Parameter(description = "Employee ID to check", required = true) @PathVariable String id) {

    CheckEmployeeExistsByIdQuery query = new CheckEmployeeExistsByIdQuery(EmployeeId.of(id));
    boolean exists = employeeQueryService.existsById(query);

		var message = String.format("Employee with ID %s exists: %b", id, exists);
    return ResponseWrapper.success(exists, message);
  }

  @GetMapping("/exists/number/{employeeNumber}")
  @RateLimit(profile = RateLimitProfile.PUBLIC)
  public ResponseWrapper<Boolean> existsByNumber(
      @Parameter(description = "Employee number to check", required = true, example = "EMP-001") @PathVariable String employeeNumber) {

    CheckEmployeeExistsByNumberQuery query = new CheckEmployeeExistsByNumberQuery(
        EmployeeNumber.of(employeeNumber));
    boolean exists = employeeQueryService.existsByNumber(query);

		var message = String.format("Employee with number %s exists: %b", employeeNumber, exists);
    return ResponseWrapper.success(exists, message);
  }
}
