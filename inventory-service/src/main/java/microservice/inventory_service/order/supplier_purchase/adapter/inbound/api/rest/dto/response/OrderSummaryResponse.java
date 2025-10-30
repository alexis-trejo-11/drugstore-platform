package microservice.inventory_service.order.supplier_purchase.adapter.inbound.api.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.OrderStatus;

import java.time.LocalDateTime;

@Schema(description = "PurchaseOrder Summary Response DTO")
@Builder
public record OrderSummaryResponse(
        @Schema(description = "Unique identifier of the order", example = "po-12345", type = "string")
        String id,
        @Schema(description = "Unique identifier of the supplier", example = "sup-67890", type = "string")
        String supplierId,
        @Schema(description = "Status of the order", example = "PENDING", type = "string")
        OrderStatus status,
        @Schema(description = "Date when the order was placed (ISO 8601)", example = "2024-02-01T10:00:00", type = "string", implementation = java.time.LocalDateTime.class)
        LocalDateTime orderDate,
        @Schema(description = "Expected delivery date of the order (ISO 8601)", example = "2024-02-10T15:30:00", type = "string", implementation = java.time.LocalDateTime.class)
        LocalDateTime expectedDeliveryDate,
        @Schema(description = "Total number of items in the order", example = "5", type = "integer")
        int itemCount
) {}