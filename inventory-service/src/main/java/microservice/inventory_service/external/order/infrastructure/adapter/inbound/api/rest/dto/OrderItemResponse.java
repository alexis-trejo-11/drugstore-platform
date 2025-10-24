package microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Schema(description = "Order Item Response DTO")
@Builder
public record OrderItemResponse(
        @Schema(description = "Identifier of the order item", example = "1", type = "integer")
        Long id,
        @Schema(description = "Unique identifier of the medicine", example = "med-98765", type = "string")
        String medicineId,
        @Schema(description = "Human-readable name of the medicine", example = "Aspirin 500mg", type = "string")
        String medicineName,
        @Schema(description = "Quantity ordered for this item", example = "10", type = "integer")
        Integer orderedQuantity,
        @Schema(description = "Quantity actually received for this item (nullable)", example = "10", type = "integer")
        Integer receivedQuantity,
        @Schema(description = "Unit cost for the item (decimal)", example = "1.25", type = "string")
        BigDecimal unitCost,
        @Schema(description = "Total cost for this item (unitCost * quantity)", example = "12.50", type = "string")
        BigDecimal totalCost,
        @Schema(description = "Batch number associated with the received item (nullable)", example = "BATCH-2024-001", type = "string")
        String batchNumber
) {
}
