package microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import microservice.inventory_service.inventory.core.inventory.domain.entity.enums.InventoryStatus;

import java.time.LocalDateTime;

@Builder
@Getter
@Schema(description = "Inventory Response DTO")
public class InventoryResponse {
    @Schema(description = "Unique identifier of the inventory", example = "inv-12345")
    private String id;

    @Schema(description = "Unique identifier of the product", example = "med-67890")
    private String productId;

    @Schema(description = "Total quantity of the inventory", example = "100")
    private Integer totalQuantity;

    @Schema(description = "Available quantity of the inventory", example = "80")
    private Integer availableQuantity;

    @Schema(description = "Reserved quantity of the inventory", example = "20")
    private Integer reservedQuantity;

    @Schema(description = "Reorder level for the inventory", example = "50")
    private Integer reorderLevel;

    @Schema(description = "Reorder quantity for the inventory", example = "100")
    private Integer reorderQuantity;

    @Schema(description = "Maximum stock level for the inventory", example = "200")
    private Integer maximumStockLevel;

    @Schema(description = "Location of the warehouse", example = "Warehouse BatchServiceImpl - Aisle 3")
    private String warehouseLocation;

    @Schema(description = "Status of the inventory", example = "IN_STOCK")
    private InventoryStatus status;

    @Schema(description = "Date when the inventory was last restocked", example = "2024-01-15T10:00:00")
    private LocalDateTime lastRestockedDate;

    @Schema(description = "Date when the inventory was last counted", example = "2024-01-20T15:30:00")
    private LocalDateTime lastCountDate;

    @Schema(description = "Number of batches associated with the inventory", example = "5")
    private int batchesCount;

    @Schema(description = "Number of movements associated with the inventory", example = "20")
    private int movementsCount;

    @Schema(description = "Timestamp when the inventory record was created", example = "2024-01-10T09:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the inventory record was last updated", example = "2024-01-18T11:45:00")
    private LocalDateTime updatedAt;
}
