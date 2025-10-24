package microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrderStatus;

import java.time.LocalDateTime;

@Schema(description = "Order Summary Response DTO")
@Builder
public record OrderSummaryResponse(
        @Schema(description = "Unique identifier of the purchase order", example = "po-12345", type = "string")
        String id,
        @Schema(description = "Order number of the purchase order", example = "ORD-2024-0001", type = "string")
        String orderNumber,
        @Schema(description = "Unique identifier of the supplier", example = "sup-67890", type = "string")
        String supplierId,
        @Schema(description = "Status of the purchase order", example = "PENDING", type = "string")
        PurchaseOrderStatus status,
        @Schema(description = "Date when the order was placed (ISO 8601)", example = "2024-02-01T10:00:00", type = "string", implementation = java.time.LocalDateTime.class)
        LocalDateTime orderDate,
        @Schema(description = "Expected delivery date of the order (ISO 8601)", example = "2024-02-10T15:30:00", type = "string", implementation = java.time.LocalDateTime.class)
        LocalDateTime expectedDeliveryDate,
        @Schema(description = "Total number of items in the order", example = "5", type = "integer")
        int itemCount
) {}