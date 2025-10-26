package microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.request;

import jakarta.validation.Valid;
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
import java.util.Currency;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsertOrderRequest {
    @NotBlank
    private String orderId;

    @NotBlank
    private String supplierId;

    @NotBlank
    private String supplierName;

    @NotEmpty(message = "Items list cannot be empty")
    @NotNull
    @Valid
    private List<OrderItemRequest> items;

    @NotNull
    @Future
    private LocalDateTime expectedDeliveryDate;

    @NotBlank(message = "Currency code is required")
    private String currencyCode;

    @NotBlank(message = "Delivery location is required")
    private String deliveryLocation;

    @NotBlank(message = "Created by is required")
    private String createdBy;

    public InsertOrderCommand toCreateCommand() {
        List<OrderItemCommand> itemCommands = items.stream()
                .map(OrderItemRequest::toCommand)
                .toList();

        return InsertOrderCommand.builder()
                .orderId(OrderId.of(orderId))
                .supplierId(supplierId)
                .supplierName(supplierName)
                .items(itemCommands)
                .currency(Currency.getInstance(currencyCode))
                .expectedDeliveryDate(expectedDeliveryDate)
                .deliveryLocation(deliveryLocation)
                .createdBy(UserId.of(createdBy))
                .isUpdate(false)
                .build();
    }

    public InsertOrderCommand toUpdateCommand(OrderId orderIdObj) {
        List<OrderItemCommand> itemCommands = items.stream()
                .map(OrderItemRequest::toCommand)
                .toList();

        return InsertOrderCommand.builder()
                .orderId(orderIdObj)
                .supplierId(supplierId)
                .supplierName(supplierName)
                .items(itemCommands)
                .expectedDeliveryDate(expectedDeliveryDate)
                .deliveryLocation(deliveryLocation)
                .currency(Currency.getInstance(currencyCode))
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
        private String itemId;

        @NotBlank
        private String productId;

        @NotBlank
        private String productName;

        @NotNull
        @Min(value = 1, message = "Quantity must be positive")
        private Integer quantity;

        @NotNull(message = "Unit cost is required")
        @DecimalMin(value = "0.01", message = "Unit cost must be positive")
        private BigDecimal unitCost;

        public OrderItemCommand toCommand() {
            return OrderItemCommand.builder()
                    .id(itemId)
                    .productId(ProductId.of(productId))
                    .productName(productName)
                    .quantity(quantity)
                    .unitCost(unitCost)
                    .build();
        }
    }
}