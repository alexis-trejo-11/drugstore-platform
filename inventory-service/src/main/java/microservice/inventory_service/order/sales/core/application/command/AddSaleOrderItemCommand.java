package microservice.inventory_service.order.sales.core.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddSaleOrderItemCommand(
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

}
