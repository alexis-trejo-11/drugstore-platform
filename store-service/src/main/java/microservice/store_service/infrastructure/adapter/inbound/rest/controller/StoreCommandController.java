package microservice.store_service.infrastructure.adapter.inbound.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.store_service.domain.port.input.StoreApplicationService;
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
@Tag(
    name = "Store Command Operations",
    description = "Endpoints for creating, updating, and managing store entities. All operations require ADMIN role authentication."
)
@SecurityRequirement(name = "bearerAuth")
public class StoreCommandController {
    private final StoreApplicationService storeApplicationFacade;

    @PostMapping
    @Operation(
        summary = "Create a new store",
        description = """
            Creates a new store in the system with complete information including:
            - Basic details (name, code, contact info)
            - Address and geolocation
            - Operating schedule
            
            **Requires JWT authentication with ADMIN role.**
            
            The store will be created with ACTIVE status by default unless otherwise specified.
            """,
        tags = {"Store Command Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Store created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseWrapper.class),
                examples = @ExampleObject(
                    name = "Store Creation Success",
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Entity created successfully",
                      "data": {
                        "storeID": "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
                      },
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Validation errors or invalid data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": false,
                      "message": "Bad request",
                      "timestamp": "2025-10-19T14:25:30",
                      "errorDetails": {
                        "errorCode": "VALIDATION_FAILED",
                        "message": "Validation errors occurred",
                        "validationErrors": {
                          "name": "Store name is required",
                          "email": "Invalid email format",
                          "phone": "Phone number must be valid"
                        }
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict - Store with the same code already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": false,
                      "message": "Conflict occurred",
                      "timestamp": "2025-10-19T14:25:30",
                      "errorDetails": {
                        "errorCode": "DUPLICATE_STORE_CODE",
                        "message": "A store with code STR-001 already exists"
                      }
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ResponseWrapper<StoreID>> createStore(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Store creation request with all required details",
            required = true,
            content = @Content(
                schema = @Schema(implementation = CreateStoreRequest.class),
                examples = @ExampleObject(
                    name = "Complete Store Creation Request",
                    value = """
                    {
                      "code": "STR-001",
                      "name": "Central Pharmacy",
                      "phone": "+1-555-0123",
                      "email": "central@pharmacy.com",
                      "address": {
                        "street": "123 Main St",
                        "city": "New York",
                        "state": "NY",
                        "zipCode": "10001",
                        "country": "USA"
                      },
                      "latitude": 40.7128,
                      "longitude": -74.0060,
                      "schedule": {
                        "monday": { "openTime": "08:00", "closeTime": "20:00" },
                        "tuesday": { "openTime": "08:00", "closeTime": "20:00" },
                        "wednesday": { "openTime": "08:00", "closeTime": "20:00" },
                        "thursday": { "openTime": "08:00", "closeTime": "20:00" },
                        "friday": { "openTime": "08:00", "closeTime": "20:00" },
                        "saturday": { "openTime": "09:00", "closeTime": "18:00" },
                        "sunday": { "openTime": "10:00", "closeTime": "16:00" }
                      }
                    }
                    """
                )
            )
        )
        @Valid @RequestBody CreateStoreRequest request
    ) {
        var command = request.toCommand();
        var result = storeApplicationFacade.createStore(command);
        var response = ResponseWrapper.created(result.storeID());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/location")
    @Operation(
        summary = "Update store location",
        description = """
            Updates the physical location and geolocation coordinates of an existing store.
            
            **Requires JWT authentication with ADMIN role.**
            
            This operation updates both the address and GPS coordinates (latitude/longitude).
            """,
        tags = {"Store Command Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Store location updated successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Operation completed successfully",
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid location data",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Store not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(mediaType = "application/json")
        )
    })
    private ResponseWrapper<Void> updateStoreLocation(
        @Parameter(description = "Store unique identifier", required = true, example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "New location details including address and coordinates",
            required = true,
            content = @Content(
                schema = @Schema(implementation = StoreLocationRequest.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "address": {
                        "street": "456 Broadway Ave",
                        "city": "Los Angeles",
                        "state": "CA",
                        "zipCode": "90001",
                        "country": "USA"
                      },
                      "latitude": 34.0522,
                      "longitude": -118.2437
                    }
                    """
                )
            )
        )
        @Valid @RequestBody StoreLocationRequest request
    ) {
        var command = request.toCommand(StoreID.of(id));
        var result = storeApplicationFacade.updateStoreLocation(command);
        return ResponseWrapper.success();
    }

    @PutMapping("/{id}/schedule")
    @Operation(
        summary = "Update store schedule",
        description = """
            Updates the operating hours and schedule of a store.
            
            **Requires JWT authentication with ADMIN role.**
            
            Can configure different hours for each day of the week or set the store as 24/7.
            """,
        tags = {"Store Command Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Store schedule updated successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Store schedule updated successfully",
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid schedule data",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Store not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(mediaType = "application/json")
        )
    })
    private ResponseWrapper<Void> updateStoreSchedule(
        @Parameter(description = "Store unique identifier", required = true, example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "New schedule configuration with operating hours per day",
            required = true,
            content = @Content(
                schema = @Schema(implementation = ScheduleInsertRequest.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "monday": { "openTime": "07:00", "closeTime": "22:00" },
                      "tuesday": { "openTime": "07:00", "closeTime": "22:00" },
                      "wednesday": { "openTime": "07:00", "closeTime": "22:00" },
                      "thursday": { "openTime": "07:00", "closeTime": "22:00" },
                      "friday": { "openTime": "07:00", "closeTime": "23:00" },
                      "saturday": { "openTime": "08:00", "closeTime": "23:00" },
                      "sunday": { "openTime": "09:00", "closeTime": "20:00" }
                    }
                    """
                )
            )
        )
        @Valid @RequestBody ScheduleInsertRequest request
    ) {
        UpdateStoreScheduleCommand command = request.toCommand(StoreID.of(id));
        var result = storeApplicationFacade.updateScheduleInfo(command);
        return ResponseWrapper.success(result.message());
    }

    @PatchMapping("/{id}/under-maintenance")
    @Operation(
        summary = "Set store under maintenance",
        description = """
            Changes the store status to UNDER_MAINTENANCE.
            
            **Requires JWT authentication with ADMIN role.**
            
            Use this when the store is temporarily closed for maintenance work.
            The store will not be available for operations until reactivated.
            """,
        tags = {"Store Command Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Store set to under maintenance successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Store status updated to UNDER_MAINTENANCE",
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Store not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(mediaType = "application/json")
        )
    })
    private ResponseWrapper<Void> setUnderMaintenance(
        @Parameter(description = "Store unique identifier", required = true, example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable String id
    ) {
        var command = new SetUnderMaintenanceCommand(StoreID.of(id));
        var result = storeApplicationFacade.setUnderMaintenance(command);
        return ResponseWrapper.success(result.message());
    }

    @PatchMapping("/{id}/temporary-closure")
    @Operation(
        summary = "Set store temporary closure",
        description = """
            Changes the store status to TEMPORARY_CLOSURE.
            
            **Requires JWT authentication with ADMIN role.**
            
            Use this for temporary closures due to unforeseen circumstances (emergencies, weather, etc.).
            The store will not accept new orders until reactivated.
            """,
        tags = {"Store Command Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Store set to temporary closure successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Operation completed successfully",
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Store not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(mediaType = "application/json")
        )
    })
    private ResponseWrapper<Void> setTemporaryClosure(
        @Parameter(description = "Store unique identifier", required = true, example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable String id
    ) {
        var command = new SetTemporaryClosureCommand(StoreID.of(id));
        var result = storeApplicationFacade.setTemporaryClosure(command);
        return ResponseWrapper.success();
    }

    @PatchMapping("/{id}/activate")
    @Operation(
        summary = "Activate store",
        description = """
            Changes the store status to ACTIVE, making it operational and available for business.
            
            **Requires JWT authentication with ADMIN role.**
            
            Use this to reactivate a store that was previously deactivated, under maintenance, or temporarily closed.
            """,
        tags = {"Store Command Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Store activated successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Store activated successfully",
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Store not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Store cannot be activated (e.g., deleted or invalid state)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(mediaType = "application/json")
        )
    })
    private ResponseWrapper<Void> activateStore(
        @Parameter(description = "Store unique identifier", required = true, example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable String id
    ) {
        var command = new ActivateStoreCommand(new StoreID(id));
        var result = storeApplicationFacade.activateStore(command);
        return ResponseWrapper.success(result.message());
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
        summary = "Deactivate store",
        description = """
            Changes the store status to INACTIVE, making it unavailable for operations.
            
            **Requires JWT authentication with ADMIN role.**
            
            Use this to temporarily disable a store without deleting it.
            The store can be reactivated later.
            """,
        tags = {"Store Command Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Store deactivated successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Store deactivated successfully",
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Store not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(mediaType = "application/json")
        )
    })
    private ResponseWrapper<Void> deactivateStore(
        @Parameter(description = "Store unique identifier", required = true, example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable String id
    ) {
        var command = new DeactivateStoreCommand(new StoreID(id));
        var result = storeApplicationFacade.deactivateStore(command);
        return ResponseWrapper.success(result.message());
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete store",
        description = """
            Permanently deletes a store from the system.
            
            **Requires JWT authentication with ADMIN role.**
            
            ⚠️ **Warning:** This is a destructive operation and cannot be undone.
            Consider deactivating the store instead if you might need to restore it later.
            
            The store must not have any active orders or pending operations before deletion.
            """,
        tags = {"Store Command Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Store deleted successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Store deleted successfully",
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Store not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Store cannot be deleted (has active dependencies)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": false,
                      "message": "Bad request",
                      "timestamp": "2025-10-19T14:25:30",
                      "errorDetails": {
                        "errorCode": "CANNOT_DELETE_STORE",
                        "message": "Store has active orders and cannot be deleted"
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(mediaType = "application/json")
        )
    })
    private ResponseWrapper<Void> deleteStore(
        @Parameter(description = "Store unique identifier to delete", required = true, example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable("id") String id
    ) {
        var command = new DeleteStoreCommand(new StoreID(id));
        var result = storeApplicationFacade.deleteStore(command);
        return ResponseWrapper.success(result.message());
    }
}
