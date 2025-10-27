package microservice.inventory_service.internal.purachse_order.adapter.inbound.api.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "PurchaseOrder Detail Response DTO")
public record PurchaseOrderResponse(
        @Schema(description = "Unique identifier of the order", example = "po-12345", type = "string")
        String id,
        @Schema(description = "PurchaseOrder number of the order", example = "ORD-2024-0001", type = "string")
        String orderNumber,
        @Schema(description = "Unique identifier of the supplier", example = "sup-67890", type = "string")
        String supplierId,
        @Schema(description = "Name of the supplier", example = "Acme Supplies Inc.", type = "string")
        String supplierName,
        @Schema(description = "List of order items", type = "array", implementation = PurchaseOrderItemResponse.class)
        List<PurchaseOrderItemResponse> items,
        @Schema(description = "Total amount for the order (decimal)", example = "123.45", type = "string")
        BigDecimal totalAmount,
        @Schema(description = "Status of the order", example = "PENDING", type = "string")
        OrderStatus status,
        @Schema(description = "Date when the order was placed (ISO 8601)", example = "2024-02-01T10:00:00", implementation = java.time.LocalDateTime.class, type = "string")
        LocalDateTime orderDate,
        @Schema(description = "Expected delivery date (ISO 8601)", example = "2024-02-10T15:30:00", implementation = java.time.LocalDateTime.class, type = "string")
        LocalDateTime expectedDeliveryDate,
        @Schema(description = "Actual delivery date if available (ISO 8601)", example = "2024-02-11T09:00:00", implementation = java.time.LocalDateTime.class, type = "string")
        LocalDateTime actualDeliveryDate,
        @Schema(description = "Delivery location address or identifier", example = "Main Warehouse - Dock 3", type = "string")
        String deliveryLocation,
        @Schema(description = "User who created the order (user id)", example = "user-123", type = "string", implementation = java.lang.String.class)
        String createdBy,
        @Schema(description = "User who approved the order (user id) - nullable", example = "user-456", type = "string", implementation = java.lang.String.class)
        String approvedBy,
        @Schema(description = "Record creation timestamp (ISO 8601)", example = "2024-02-01T10:00:00", implementation = java.time.LocalDateTime.class, type = "string")
        LocalDateTime createdAt,
        @Schema(description = "Record last update timestamp (ISO 8601)", example = "2024-02-02T12:00:00", implementation = java.time.LocalDateTime.class, type = "string")
        LocalDateTime updatedAt
) {
}
