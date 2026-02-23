package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.controller;

import io.github.alexisTrejo11.drugstore.employees.core.application.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.alexisTrejo11.drugstore.employees.core.port.input.EmployeeCommandService;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;
import io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.annotation.*;
import io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.request.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;

/**
 * REST Controller for employee command operations (write operations)
 */
@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Command Operations", description = "Endpoints for creating, updating, and managing employee entities. All operations require authentication with appropriate roles.")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeCommandController {

	private final EmployeeCommandService employeeCommandService;

	@Autowired
	public EmployeeCommandController(EmployeeCommandService employeeCommandService) {
		this.employeeCommandService = employeeCommandService;
	}

	@PostMapping
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	@CreateEmployeeOperation
	public ResponseEntity<ResponseWrapper<EmployeeId>> createEmployee(
			@Valid @RequestBody @NotNull CreateEmployeeRequest request) {

		CreateEmployeeCommand command = request.toCommand();
		EmployeeId employeeId = employeeCommandService.createEmployee(command);

		var response = ResponseWrapper.created(employeeId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	@RateLimit(profile = RateLimitProfile.STANDARD)
	@UpdateEmployeeOperation
	public ResponseWrapper<Void> updateEmployee(
			@EmployeeIdPathParameter @PathVariable String id,
			@Valid @RequestBody @NotNull UpdateEmployeeRequest request) {

		UpdateEmployeeCommand command = request.toCommand(EmployeeId.of(id));
		employeeCommandService.updateEmployee(command);

		return ResponseWrapper.success("Employee updated successfully");
	}

	@PostMapping("/{id}/certifications")
	@RateLimit(profile = RateLimitProfile.STANDARD)
	@AddCertificationOperation
	public ResponseWrapper<Void> addCertification(
			@EmployeeIdPathParameter @PathVariable String id,
			@Valid @RequestBody @NotNull AddCertificationRequest request) {

		AddCertificationCommand command = request.toCommand(EmployeeId.of(id));
		employeeCommandService.addCertification(command);

		return ResponseWrapper.success("Certification added successfully");
	}

	@PatchMapping("/{id}/role")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseWrapper<Void> changeRole(
			@EmployeeIdPathParameter @PathVariable String id,
			@Valid @RequestBody @NotNull ChangeRoleRequest request) {

		ChangeRoleCommand command = request.toCommand(EmployeeId.of(id));
		employeeCommandService.changeRole(command);

		return ResponseWrapper.success("Employee role changed successfully");
	}

	@PatchMapping("/{id}/status")
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseWrapper<Void> changeStatus(
			@EmployeeIdPathParameter @PathVariable String id,
			@Valid @RequestBody @NotNull ChangeStatusRequest request) {

		ChangeStatusCommand command = request.toCommand(EmployeeId.of(id));
		employeeCommandService.changeStatus(command);

		return ResponseWrapper.success("Employee status changed successfully");
	}

	@PatchMapping("/{id}/compensation")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseWrapper<Void> updateCompensation(
			@EmployeeIdPathParameter @PathVariable String id,
			@Valid @RequestBody @NotNull UpdateCompensationRequest request) {

		UpdateCompensationCommand command = request.toCommand(EmployeeId.of(id));
		employeeCommandService.updateCompensation(command);

		return ResponseWrapper.success("Compensation updated successfully");
	}

	@PatchMapping("/{id}/suspend")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseWrapper<Void> suspendEmployee(
			@EmployeeIdPathParameter @PathVariable String id,
			@RequestParam @NotNull String reason,
			@RequestParam @NotNull String suspendedBy) {

		SuspendEmployeeCommand command = new SuspendEmployeeCommand(
				EmployeeId.of(id), reason, suspendedBy);
		employeeCommandService.suspendEmployee(command);

		return ResponseWrapper.success("Employee suspended successfully");
	}

	@PatchMapping("/{id}/activate")
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseWrapper<Void> activateEmployee(
			@EmployeeIdPathParameter @PathVariable String id,
			@RequestParam @NotNull String reason,
			@RequestParam @NotNull String activatedBy) {

		ActivateEmployeeCommand command = new ActivateEmployeeCommand(
				EmployeeId.of(id), reason, activatedBy);
		employeeCommandService.activateEmployee(command);

		return ResponseWrapper.success("Employee activated successfully");
	}

	@PatchMapping("/{id}/on-leave")
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseWrapper<Void> putOnLeave(
			@EmployeeIdPathParameter @PathVariable String id,
			@RequestParam @NotNull String reason,
			@RequestParam @NotNull String approvedBy) {

		PutOnLeaveCommand command = new PutOnLeaveCommand(
				EmployeeId.of(id), reason, approvedBy);
		employeeCommandService.putOnLeave(command);

		return ResponseWrapper.success("Employee put on leave successfully");
	}

	@DeleteMapping("/{id}")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	@DeleteEmployeeOperation
	public ResponseWrapper<Void> deleteEmployee(
			@EmployeeIdPathParameter @PathVariable String id,
			@RequestParam @NotNull String deletedBy) {

		DeleteEmployeeCommand command = new DeleteEmployeeCommand(
				EmployeeId.of(id), deletedBy);
		employeeCommandService.deleteEmployee(command);

		return ResponseWrapper.success("Employee deleted successfully");
	}

	@PatchMapping("/{id}/restore")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseWrapper<Void> restoreEmployee(
			@EmployeeIdPathParameter @PathVariable String id,
			@RequestParam @NotNull String restoredBy) {

		RestoreEmployeeCommand command = new RestoreEmployeeCommand(
				EmployeeId.of(id), restoredBy);
		employeeCommandService.restoreEmployee(command);

		return ResponseWrapper.success("Employee restored successfully");
	}
}
