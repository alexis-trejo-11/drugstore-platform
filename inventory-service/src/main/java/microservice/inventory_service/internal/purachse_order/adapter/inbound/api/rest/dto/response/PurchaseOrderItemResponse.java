package microservice.inventory_service.internal.purachse_order.adapter.inbound.api.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Schema(description = "PurchaseOrder Item Response DTO")
@Builder
public record PurchaseOrderItemResponse(
        @Schema(description = "Identifier of the order item", example = "1", type = "integer")
        Long id,
        @Schema(description = "Unique identifier of the product", example = "med-98765", type = "string")
        String productId,
        @Schema(description = "Human-readable name of the product", example = "Aspirin 500mg", type = "string")
        String productName,
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
