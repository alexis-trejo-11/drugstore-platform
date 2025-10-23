package microservice.inventory_service.inventory.infrastructure.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.*;
import microservice.inventory_service.inventory.application.command.AddInventoryBatchCommand;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateInventoryBatchRequest(
    @NotNull @NotBlank String inventoryId,
    @NotNull @NotBlank String batchNumber,
    @NotNull @NotBlank String lotNumber,
    @NotNull @Positive Integer quantity,
    @NotNull @Positive BigDecimal costPerUnit,
    @Past LocalDateTime manufacturingDate,
    @NotNull @Future LocalDateTime expirationDate,
    @NotNull @NotBlank String supplierId,
    @NotBlank String supplierName,
    @NotBlank String storageConditions
){
    public AddInventoryBatchCommand toCommand() {
        return AddInventoryBatchCommand.builder()
                .inventoryId(new InventoryId(this.inventoryId))
                .batchNumber(this.batchNumber)
                .lotNumber(this.lotNumber)
                .quantity(this.quantity)
                .costPerUnit(this.costPerUnit)
                .manufacturingDate(this.manufacturingDate)
                .expirationDate(this.expirationDate)
                .supplierId(this.supplierId)
                .supplierName(this.supplierName)
                .storageConditions(this.storageConditions)
                .build();
    }
}

