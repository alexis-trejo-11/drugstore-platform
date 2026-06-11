package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductID;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.SKU;
import libs_kernel.exceptions.NotFoundException;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException(ProductID productID) {
        super("Product", "ID", productID.value());
    }

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(SKU sku) {
        super("Product", "SKU", sku.value());
    }
}
