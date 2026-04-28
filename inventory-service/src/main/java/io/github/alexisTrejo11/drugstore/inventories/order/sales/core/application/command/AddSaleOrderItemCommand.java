package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;
import io.github.alexisTrejo11.drugstore.inventories.shared.exception.MissingFieldException;

public record AddSaleOrderItemCommand(
        @NotNull String id,
        @NotBlank ProductId productId,
        @NotBlank String productName,
        @NotBlank Integer quantity
) {

    public void validate() {
        if (id == null) throw new MissingFieldException("AddSaleOrderItemCommand", "id");
        if (productId == null) throw new MissingFieldException("AddSaleOrderItemCommand", "productId");
        if (productName == null || productName.isBlank())
            throw new MissingFieldException("AddSaleOrderItemCommand", "productName");
        if (quantity == null || quantity <= 0) throw new MissingFieldException("AddSaleOrderItemCommand", "quantity");

        if (quantity >= 20) {
            throw new IllegalArgumentException("Quantity must be less than 20.");
        }
    }

}
