package microservice.product_service.app.domain.model;

import java.util.List;

import lombok.Builder;
import microservice.product_service.app.domain.model.enums.ProductStatus;
import microservice.product_service.app.domain.model.valueobjects.*;

@Builder
public record ReconstructProductParams(
    ProductID id,
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
    Administration administration,
    ProductTimeStamps timeStamps) {
}
