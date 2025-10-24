package microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.*;
import microservice.inventory_service.internal.core.inventory.application.cqrs.command.CreateInventoryCommand;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.MedicineId;
import org.hibernate.validator.constraints.Length;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

@Schema(description = "Request to create a new inventory for a medicine")
public record CreateInventoryRequest(
        @Schema(description = "Unique identifier of the medicine", example = "med-98765", type = "string")
        @NotNull @NotBlank @Length(max = 50) String medicineId,

        @Schema(description = "Reorder threshold to trigger restock", example = "10", type = "integer")
        @NotNull @Positive Integer reorderLevel,

        @Schema(description = "Quantity to reorder when threshold is reached", example = "50", type = "integer")
        @NotNull @Positive Integer reorderQuantity,

        @Schema(description = "Maximum stock level allowed in inventory", example = "500", type = "integer")
        @NotNull @Positive Integer maximumStockLevel,

        @Schema(description = "Warehouse location or storage area", example = "Main Warehouse - Aisle 3", type = "string")
        @NotNull @NotBlank @Length(max = 255) String warehouseLocation,

        @Schema(description = "Optional initial batch to create together with the inventory", implementation = AddInventoryBatchRequest.class)
        AddInventoryBatchRequest initialBatch
) {
    public CreateInventoryCommand toCommand() {
        return CreateInventoryCommand.builder()
                .medicineId(new MedicineId(this.medicineId))
                .reorderLevel(this.reorderLevel)
                .reorderQuantity(this.reorderQuantity)
                .maximumStockLevel(this.maximumStockLevel)
                .warehouseLocation(this.warehouseLocation)
                .batchCommand(initialBatch != null ? Optional.of(initialBatch.toCommand()) : Optional.empty())
                .build();
    }
}
