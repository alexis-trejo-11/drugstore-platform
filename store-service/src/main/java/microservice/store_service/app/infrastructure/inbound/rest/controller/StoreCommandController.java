package microservice.store_service.app.infrastructure.inbound.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;
import microservice.store_service.app.application.port.in.command.*;
import microservice.store_service.app.application.port.in.command.status.ActivateStoreCommand;
import microservice.store_service.app.application.port.in.command.status.DeactivateStoreCommand;
import microservice.store_service.app.application.port.in.query.CreateStoreResult;
import microservice.store_service.app.application.port.in.usecase.StoreCommandUseCases;
import microservice.store_service.app.domain.model.valueobjects.StoreID;
import microservice.store_service.app.infrastructure.inbound.rest.annotation.*;
import microservice.store_service.app.infrastructure.inbound.rest.dto.request.CreateStoreRequest;
import microservice.store_service.app.infrastructure.inbound.rest.dto.request.ScheduleInsertRequest;
import microservice.store_service.app.infrastructure.inbound.rest.dto.request.StoreLocationRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v2/stores")
@Tag(
		name = "Store Command Operations",
		description = "Endpoints for creating, updating, and managing store entities. All operations require ADMIN role authentication."
)
@SecurityRequirement(name = "bearerAuth")
public class StoreCommandController {
	private final StoreCommandUseCases storeCommandUseCases;

	@Autowired
	public StoreCommandController(StoreCommandUseCases storeCommandUseCases) {
		this.storeCommandUseCases = storeCommandUseCases;
	}

	@PostMapping
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	@CreateStoreOperation
	public ResponseEntity<ResponseWrapper<StoreID>> createStore(
			@CreateStoreBodyParameter
			@Valid @RequestBody @NotNull CreateStoreRequest request
	) {
		CreateStoreCommand command = request.toCommand();
		CreateStoreResult result = storeCommandUseCases.createStore(command);

		var response = ResponseWrapper.created(result.storeID());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}/location")
	@RateLimit(profile = RateLimitProfile.STANDARD)
	@UpdateStoreLocationOperation
	public ResponseWrapper<Void> updateStoreLocation(
			@StoreIdURLParameter @PathVariable String id,
			@UpdateStoreLocationBodyParameters @Valid @RequestBody @NotNull StoreLocationRequest request
	) {
		var command = request.toCommand(StoreID.of(id));
		var result = storeCommandUseCases.updateStoreLocation(command);
		return ResponseWrapper.success();
	}

	@PutMapping("/{id}/schedule")
	@RateLimit(profile = RateLimitProfile.STANDARD)
	@UpdateStoreScheduleOperation
	public ResponseWrapper<Void> updateStoreSchedule(
			@StoreIdURLParameter @PathVariable String id,
			@UpdateStoreScheduleBodyParameters @Valid @RequestBody @NotNull ScheduleInsertRequest request
	) {
		UpdateStoreScheduleCommand command = request.toCommand(StoreID.of(id));
		var result = storeCommandUseCases.updateScheduleInfo(command);
		return ResponseWrapper.success(result.message());
	}

	@PatchMapping("/{id}/under-maintenance")
	@RateLimit(profile = RateLimitProfile.STANDARD)
	@SetUnderMaintenanceOperation
	public ResponseWrapper<Void> setUnderMaintenance(
			@StoreIdURLParameter @PathVariable String id
	) {
		var command = new SetUnderMaintenanceCommand(StoreID.of(id));
		var result = storeCommandUseCases.setUnderMaintenance(command);
		return ResponseWrapper.success(result.message());
	}

	@PatchMapping("/{id}/temporary-closure")
	@SetTemporaryClosureOperation
	public ResponseWrapper<Void> setTemporaryClosure(
			@StoreIdURLParameter @PathVariable String id
	) {
		var command = new SetTemporaryClosureCommand(StoreID.of(id));
		var result = storeCommandUseCases.setTemporaryClosure(command);
		return ResponseWrapper.success();
	}

	@PatchMapping("/{id}/activate")
	@RateLimit(profile = RateLimitProfile.STANDARD)
	@ActivateStoreOperation
	public ResponseWrapper<Void> activateStore(
			@StoreIdURLParameter @PathVariable String id
	) {
		var command = new ActivateStoreCommand(new StoreID(id));
		var result = storeCommandUseCases.activateStore(command);
		return ResponseWrapper.success(result.message());
	}

	@PatchMapping("/{id}/deactivate")
	@DeactivateStoreOperation
	public ResponseWrapper<Void> deactivateStore(
			@StoreIdURLParameter @PathVariable String id
	) {
		var command = new DeactivateStoreCommand(new StoreID(id));
		var result = storeCommandUseCases.deactivateStore(command);
		return ResponseWrapper.success(result.message());
	}

	@DeleteMapping("/{id}")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	@DeleteStoreOperation
	public ResponseWrapper<Void> deleteStore(
			@StoreIdURLParameter @PathVariable("id") String id
	) {
		var command = new DeleteStoreCommand(new StoreID(id));
		var result = storeCommandUseCases.deleteStore(command);
		return ResponseWrapper.success(result.message());
	}
}
