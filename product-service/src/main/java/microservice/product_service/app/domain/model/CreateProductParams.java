package microservice.product_service.app.domain.model;

import java.util.List;

import lombok.Builder;
import microservice.product_service.app.domain.model.enums.ProductStatus;
import microservice.product_service.app.domain.model.valueobjects.ActiveIngredient;
import microservice.product_service.app.domain.model.valueobjects.Administration;
import microservice.product_service.app.domain.model.valueobjects.Dosage;
import microservice.product_service.app.domain.model.valueobjects.ExpirationRange;
import microservice.product_service.app.domain.model.valueobjects.Manufacturer;
import microservice.product_service.app.domain.model.valueobjects.ProductClassification;
import microservice.product_service.app.domain.model.valueobjects.ProductDescription;
import microservice.product_service.app.domain.model.valueobjects.ProductImages;
import microservice.product_service.app.domain.model.valueobjects.ProductName;
import microservice.product_service.app.domain.model.valueobjects.ProductPrice;
import microservice.product_service.app.domain.model.valueobjects.SKU;

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
