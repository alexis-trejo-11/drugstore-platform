package microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.command.AdjustInventoryCommand;
import microservice.inventory_service.inventory.core.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjustInventoryRequest {

    private String batchId;

    @NotNull(message = "Quantity adjustment is required")
    private Integer quantityAdjustment;

    @NotNull(message = "Adjustment reason is required")
    private AdjustmentReason reason;

    private String notes;

    @NotBlank(message = "Performed by is required")
    private String performedBy;

    private String approvedBy;

    public AdjustInventoryCommand toCommand(String inventoryId) {
        return AdjustInventoryCommand.builder()
                .inventoryId(InventoryId.of(inventoryId))
                .batchId(batchId != null ? BatchId.of(batchId) : null)
                .quantityAdjustment(quantityAdjustment)
                .reason(reason)
                .notes(notes)
                .performedBy(UserId.of(performedBy))
                .approvedBy(approvedBy != null ? UserId.of(approvedBy) : null)
                .build();
    }
}