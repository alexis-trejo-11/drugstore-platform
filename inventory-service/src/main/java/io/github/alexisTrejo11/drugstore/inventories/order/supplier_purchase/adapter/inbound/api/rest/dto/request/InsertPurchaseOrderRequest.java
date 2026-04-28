package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.adapter.inbound.api.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command.InitOrderCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command.OrderItemCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Request DTO to create or update a purchase order")
public record InsertPurchaseOrderRequest(
        @Schema(description = "Unique identifier of the purchase order", example = "po-12345")
        @NotBlank
        String purchaseOrderId,

        @Schema(description = "Unique identifier of the supplier", example = "sup-987")
        @NotBlank
        String supplierId,

        @Schema(description = "Human-readable supplier name", example = "Acme Pharma Ltd.")
        @NotBlank
        String supplierName,

        @Schema(description = "List of items included in the purchase order", required = true)
        @NotNull
        @NotEmpty(message = "Items list cannot be empty")
        @Valid
        List<OrderItemRequest> items,

        @Schema(description = "Expected delivery date and time (ISO-8601)", example = "2025-11-01T10:00:00")
        @NotNull
        @Future
        LocalDateTime expectedDeliveryDate,

        @Schema(description = "Delivery location or address where the order should be delivered", example = "Warehouse A, 123 Main St")
        @NotBlank(message = "Delivery location is required")
        String deliveryLocation,

        @Schema(description = "Identifier of the user who created the purchase order", example = "user-42")
        @NotBlank(message = "Created by is required")
        String createdBy
) {

    public InitOrderCommand toCreateCommand() {
        List<OrderItemCommand> itemCommands = items.stream()
                .map(OrderItemRequest::toCommand)
                .toList();

        return InitOrderCommand.builder()
                .purchaseOrderId(PurchaseOrderId.of(purchaseOrderId))
                .supplierId(supplierId)
                .supplierName(supplierName)
                .items(itemCommands)
                .expectedDeliveryDate(expectedDeliveryDate)
                .deliveryLocation(deliveryLocation)
                .createdBy(UserId.of(createdBy))
                .isUpdate(false)
                .build();
    }

    public InitOrderCommand toUpdateCommand(PurchaseOrderId purchaseOrderIdObj) {
        List<OrderItemCommand> itemCommands = items.stream()
                .map(OrderItemRequest::toCommand)
                .toList();

        return InitOrderCommand.builder()
                .purchaseOrderId(purchaseOrderIdObj)
                .supplierId(supplierId)
                .supplierName(supplierName)
                .items(itemCommands)
                .expectedDeliveryDate(expectedDeliveryDate)
                .deliveryLocation(deliveryLocation)
                .createdBy(UserId.of(createdBy))
                .isUpdate(true)
                .build();
    }

    @Schema(description = "Single item within a purchase order")
    public static record OrderItemRequest(
            @Schema(description = "Identifier of the order item", example = "item-1")
            @NotBlank
            String itemId,

            @Schema(description = "Unique identifier of the product", example = "prod-98765")
            @NotBlank
            String productId,

            @Schema(description = "Human-readable name of the product", example = "Aspirin 500mg")
            @NotBlank
            String productName,

            @Schema(description = "Quantity ordered for this item", example = "10")
            @NotNull
            @Min(value = 1, message = "Quantity must be positive")
            Integer quantity
    ) {
        public OrderItemCommand toCommand() {
            return OrderItemCommand.builder()
                    .id(itemId)
                    .productId(ProductId.of(productId))
                    .productName(productName)
                    .quantity(quantity)
                    .build();
        }
    }
}