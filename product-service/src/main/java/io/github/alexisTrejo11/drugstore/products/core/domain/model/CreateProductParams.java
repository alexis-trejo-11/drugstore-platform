package io.github.alexisTrejo11.drugstore.products.core.domain.model;

import java.util.List;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.enums.ProductStatus;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ActiveIngredient;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.Administration;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.Dosage;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ExpirationRange;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.Manufacturer;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductClassification;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductDescription;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductImages;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductName;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductPrice;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.SKU;

@Builder
public record CreateProductParams(
    SKU sku,
    String barcode,
    ProductName name,
    ProductDescription description,
    Manufacturer manufacturer,
    ProductImages images,
    ProductClassification classification,
    ProductPrice price,
    ExpirationRange expirationRange,
    ActiveIngredient activeIngredient,
    boolean requiresPrescription,
    ProductStatus status,
    List<String> contraindications,
    Dosage dosage,
    Administration administration) {
}
