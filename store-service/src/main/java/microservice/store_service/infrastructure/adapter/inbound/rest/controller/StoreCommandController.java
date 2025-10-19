package microservice.store_service.infrastructure.adapter.inbound.rest.controller;

import jakarta.validation.Valid;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.store_service.application.StoreApplicationService;
import microservice.store_service.application.dto.command.DeleteStoreCommand;
import microservice.store_service.application.dto.command.UpdateStoreScheduleCommand;
import microservice.store_service.application.dto.command.status.ActivateStoreCommand;
import microservice.store_service.application.dto.command.status.DeactivateStoreCommand;
import microservice.store_service.application.dto.command.SetTemporaryClosureCommand;
import microservice.store_service.application.dto.command.SetUnderMaintenanceCommand;
import microservice.store_service.domain.model.valueobjects.StoreID;
import microservice.store_service.infrastructure.adapter.inbound.rest.dto.request.CreateStoreRequest;
import microservice.store_service.infrastructure.adapter.inbound.rest.dto.request.ScheduleInsertRequest;
import microservice.store_service.infrastructure.adapter.inbound.rest.dto.request.StoreLocationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/stores")
public class StoreCommandController {
    private final StoreApplicationService storeApplicationFacade;

    @PostMapping
    public ResponseEntity<ResponseWrapper<StoreID>> createStore(@Valid @RequestBody CreateStoreRequest request) {
        var command = request.toCommand();
        var result = storeApplicationFacade.createStore(command);
        var response = ResponseWrapper.created(result.storeID());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/location")
    private ResponseWrapper<Void> updateStoreLocation(@PathVariable String id, @Valid @RequestBody StoreLocationRequest request) {
        var command = request.toCommand(StoreID.of(id));
        var result = storeApplicationFacade.updateStoreLocation(command);
        return ResponseWrapper.success();
    }

    @PatchMapping("/{id}/schedule")
    private ResponseWrapper<Void> updateStoreSchedule(@PathVariable String id, @Valid @RequestBody ScheduleInsertRequest request) {
        UpdateStoreScheduleCommand command = request.toCommand(StoreID.of(id));
        var result = storeApplicationFacade.updateScheduleInfo(command);
        return ResponseWrapper.success(result.message());
    }

    @PatchMapping("/{id}/under-maintenance")
    private ResponseWrapper<Void> setUnderMaintenance(@PathVariable String id) {
        var command = new SetUnderMaintenanceCommand(StoreID.of(id));
        var result = storeApplicationFacade.setUnderMaintenance(command);
        return ResponseWrapper.success(result.message());
    }

    @PatchMapping("/{id}/temporary-closure")
    private ResponseWrapper<Void> setTemporaryClosure(@PathVariable String id, @Valid @RequestBody CreateStoreRequest request) {
        var command = new SetTemporaryClosureCommand(StoreID.of(id));
        var result = storeApplicationFacade.setTemporaryClosure(command);
        return ResponseWrapper.success();
    }

    @PatchMapping("/{id}/activate")
    private ResponseWrapper<Void> activateStore(@PathVariable String id) {
        var command = new ActivateStoreCommand(new StoreID(id));
        var result = storeApplicationFacade.activateStore(command);
        return ResponseWrapper.success(result.message());
    }

    @PatchMapping("/{id}/deactivate")
    private ResponseWrapper<Void> deactivateStore(@PathVariable String id) {
        var command = new DeactivateStoreCommand(new StoreID(id));
        var result = storeApplicationFacade.deactivateStore(command);
        return ResponseWrapper.success(result.message());
    }

    @DeleteMapping
    private ResponseWrapper<Void> deleteStore(@PathVariable("id") String id) {
        var command = new DeleteStoreCommand(new StoreID(id));
        var result = storeApplicationFacade.deleteStore(command);
        return ResponseWrapper.success(result.message());
    }
}
