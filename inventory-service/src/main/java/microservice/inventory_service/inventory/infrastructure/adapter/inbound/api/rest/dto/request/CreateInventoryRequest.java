package microservice.inventory_service.inventory.infrastructure.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.*;
import microservice.inventory_service.inventory.application.command.CreateInventoryCommand;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.MedicineId;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Optional;

public record CreateInventoryRequest(
        @NotNull @NotBlank @Length(max = 50) String medicineId,
        @NotNull @PositiveOrZero Integer initialQuantity,
        @NotNull @Positive Integer reorderLevel,
        @NotNull @Positive Integer reorderQuantity,
        @NotNull @Positive Integer maximumStockLevel,
        @NotNull @NotBlank @Length(max = 255) String warehouseLocation,
        CreateInventoryBatchRequest batches
) {
    public CreateInventoryCommand toCommand() {
        return CreateInventoryCommand.builder()
                .medicineId(new MedicineId(this.medicineId))
                .initialQuantity(this.initialQuantity)
                .reorderLevel(this.reorderLevel)
                .reorderQuantity(this.reorderQuantity)
                .maximumStockLevel(this.maximumStockLevel)
                .warehouseLocation(this.warehouseLocation)
                .batchCommand(Optional.of(this.batches.toCommand()))
                .build();
    }

}
