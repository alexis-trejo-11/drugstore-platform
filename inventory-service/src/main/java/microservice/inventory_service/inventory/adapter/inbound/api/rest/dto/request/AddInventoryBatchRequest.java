package microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.*;
import microservice.inventory_service.inventory.core.batch.application.command.RegisterInventoryBatchCommand;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Request to create StockMovementUseCaseImpl new inventory batch for an existing inventory")
public record AddInventoryBatchRequest(
    @Schema(description = "Batch number assigned by supplier or internal system", example = "BATCH-2024-001", type = "string")
    @NotNull @NotBlank String batchNumber,

    @Schema(description = "Supplier lot number for traceability", example = "LOT-555", type = "string")
    @NotNull @NotBlank String lotNumber,

    @Schema(description = "Quantity received in this batch", example = "100", type = "integer")
    @NotNull @Positive Integer quantity,

    @Schema(description = "Manufacturing date of the batch (ISO 8601) - optional", example = "2024-01-01T00:00:00", implementation = java.time.LocalDateTime.class, type = "string")
    @Past LocalDateTime manufacturingDate,

    @Schema(description = "Expiration date for the batch (ISO 8601)", example = "2025-01-01T00:00:00", implementation = java.time.LocalDateTime.class, type = "string")
    @NotNull @Future LocalDateTime expirationDate,

    @Schema(description = "Unique identifier of the supplier", example = "sup-67890", type = "string")
    @NotNull @NotBlank String supplierId,

    @Schema(description = "Name of the supplier", example = "Acme Supplies Inc.", type = "string")
    @NotBlank String supplierName,

    @Schema(description = "Storage instructions or conditions for the batch", example = "Store between 2-8°C", type = "string")
    @NotBlank String storageConditions
){
    public RegisterInventoryBatchCommand toCommand() {
        return RegisterInventoryBatchCommand.builder()
                .batchNumber(this.batchNumber)
                .lotNumber(this.lotNumber)
                .quantity(this.quantity)
                .manufacturingDate(this.manufacturingDate)
                .expirationDate(this.expirationDate)
                .supplierId(this.supplierId)
                .supplierName(this.supplierName)
                .storageConditions(this.storageConditions)
                .build();
    }

    public RegisterInventoryBatchCommand toCommand(String inventoryId) {
        return RegisterInventoryBatchCommand.builder()
                .inventoryId(new InventoryId(inventoryId))
                .batchNumber(this.batchNumber)
                .lotNumber(this.lotNumber)
                .quantity(this.quantity)
                .manufacturingDate(this.manufacturingDate)
                .expirationDate(this.expirationDate)
                .supplierId(this.supplierId)
                .supplierName(this.supplierName)
                .storageConditions(this.storageConditions)
                .build();
    }
}
