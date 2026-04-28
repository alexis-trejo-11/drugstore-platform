package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductID;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.SKU;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(ProductID id) {
        super("Product with ID " + id.value() + " not found.");
    }

    public ProductNotFoundException(SKU sku) {
        super("Product with SKU " + sku.value() + " not found.");
    }

    public  ProductNotFoundException(String message) {
        super(message);

    }
}

