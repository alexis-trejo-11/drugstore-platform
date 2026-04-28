package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.inbound.api.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response DTO representing an inventory batch")
public class BatchResponse {
    @Schema(description = "Unique identifier of the inventory batch", example = "batch-12345")
    private String id;

    @Schema(description = "Identifier of the associated inventory", example = "inventory-67890")
    private String batchNumber;

    @Schema(description = "Lot number of the batch", example = "LOT-2024-001")
    private String lotNumber;

    @Schema(description = "Price per unit of the batch", example = "19.99")
    private Integer quantity;

    @Schema(description = "Available quantity in the batch", example = "15")
    private Integer availableQuantity;

    @Schema(description = "Price per unit of the batch", example = "19.99")
    private LocalDateTime manufacturingDate;

    @Schema(description = "Expiration date of the batch", example = "2025-12-31T23:59:59")
    private LocalDateTime expirationDate;

    @Schema(description = "Identifier of the supplier", example = "supplier-54321")
    private String supplierId;

    @Schema(description = "Name of the supplier", example = "Acme Supplies Co.")
    private String supplierName;

    @Schema(description = "Status of the batch", example = "AVAILABLE")
    private BatchStatus status;

    @Schema(description = "Storage conditions for the batch", example = "Keep refrigerated between 2-8°C")
    private String storageConditions;

    @Schema(description = "Days remaining until the batch expires", example = "120")
    private Long daysUntilExpiration;

    @Schema(description = "Indicates if the batch is expired", example = "false")
    private Boolean isExpired;

    @Schema(description = "Indicates if the batch is expiring soon", example = "true")
    private Boolean isExpiringSoon;

    @Schema(description = "Date when the batch was received", example = "2024-06-15T10:30:00")
    private LocalDateTime receivedDate;

    @Schema(description = "Date when the batch was created", example = "2024-06-01T09:00:00")
    private LocalDateTime createdAt;
}
