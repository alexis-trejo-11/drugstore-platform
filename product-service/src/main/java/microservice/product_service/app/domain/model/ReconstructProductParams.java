package microservice.product_service.app.domain.model;

import lombok.Builder;
import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.enums.ProductStatus;
import microservice.product_service.app.domain.model.valueobjects.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
public record ReconstructProductParams(
		ProductID id,
		ProductCode code,
		String barcode,
		ProductName name,
		ProductDescription description,
		ActiveIngredient activeIngredient,
		Manufacturer manufacturer,
		ProductCategory category,
		ProductPrice price,
		ProductDates dates,
		boolean requiresPrescription,
		ProductStatus status,
		Set<String> contraindications,
		Dosage dosage,
		Administration administration,
		ProductTimeStamps timeStamps
) {
}

