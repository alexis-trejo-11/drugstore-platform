package microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.external.order.application.command.InsertOrderCommand;
import microservice.inventory_service.external.order.application.command.OrderItemCommand;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotNull
    private String orderId;

    @NotBlank
    @NotNull
    private String supplierId;

    @NotBlank
    @NotNull
    private String supplierName;

    @NotEmpty(message = "Items list cannot be empty")
    @NotNull
    private List<OrderItemRequest> items;

    @NotNull(message = "Expected delivery date is required")
    @Future(message = "Expected delivery date must be in the future")
    private LocalDateTime expectedDeliveryDate;

    @NotBlank(message = "Delivery location is required")
    private String deliveryLocation;

    @NotBlank(message = "Created by is required")
    private String createdBy;

    public InsertOrderCommand toCreateCommand() {
        List<OrderItemCommand> itemCommands = items.stream()
                .map(OrderItemRequest::toCommand)
                .toList();

        return InsertOrderCommand.builder()
                .orderId(orderId != null ? OrderId.of(orderId) : null)
                .supplierId(supplierId)
                .supplierName(supplierName)
                .items(itemCommands)
                .expectedDeliveryDate(expectedDeliveryDate)
                .deliveryLocation(deliveryLocation)
                .createdBy(UserId.of(createdBy))
                .isUpdate(false)
                .build();
    }

    public InsertOrderCommand toUpdateCommand() {
        List<OrderItemCommand> itemCommands = items.stream()
                .map(OrderItemRequest::toCommand)
                .toList();

        return InsertOrderCommand.builder()
                .orderId(OrderId.of(orderId))
                .supplierId(supplierId)
                .supplierName(supplierName)
                .items(itemCommands)
                .expectedDeliveryDate(expectedDeliveryDate)
                .deliveryLocation(deliveryLocation)
                .createdBy(UserId.of(createdBy))
                .isUpdate(true)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {

        @NotBlank
        @NotNull
        private String productId;

        @NotBlank
        @NotNull
        private String productName;

        @NotNull
        @NotNull
        @Min(value = 1, message = "Quantity must be positive")
        private Integer quantity;

        @NotNull(message = "Unit cost is required")
        @DecimalMin(value = "0.01", message = "Unit cost must be positive")
        private BigDecimal unitCost;

        public OrderItemCommand toCommand() {
            return OrderItemCommand.builder()
                    .productId(ProductId.of(productId))
                    .productName(productName)
                    .quantity(quantity)
                    .unitCost(unitCost)
                    .build();
        }
    }
}