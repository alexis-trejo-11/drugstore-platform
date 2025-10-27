package microservice.inventory_service.external.sales_order.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import microservice.inventory_service.external.sales_order.model.SaleOrderEntity;
import microservice.inventory_service.external.sales_order.model.SaleOrderItemEntity;

public record SaleOrderItemDTO(
        @NotNull String id,
        @NotBlank String productId,
        @NotBlank String productName,
        @NotBlank Integer quantity
) {

    public void validate() {
        if (id == null) throw new IllegalArgumentException("Item ID cannot be null");
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product ID cannot be null or blank");
        if (productName == null || productName.isBlank()) throw new IllegalArgumentException("Product name cannot be null or blank");
        if (quantity == null || quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
    }

    public SaleOrderItemEntity toEntity(String orderId) {
        return SaleOrderItemEntity.builder()
                .id(id)
                .saleOrder(new SaleOrderEntity(orderId))
                .productId(productId)
                .productName(productName)
                .quantity(quantity)
                .build();
    }
}
