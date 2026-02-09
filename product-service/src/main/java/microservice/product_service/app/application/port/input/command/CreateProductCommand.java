package microservice.product_service.app.application.port.input.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import microservice.product_service.app.domain.model.CreateProductParams;
import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.enums.ProductStatus;
import microservice.product_service.app.domain.model.enums.ProductSubcategory;
import microservice.product_service.app.domain.model.enums.ProductType;
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
public record CreateProductCommand(
    String sku,
    String name,
    String description,
    String activeIngredient,
    String manufacturer,
    ProductType type,
    ProductCategory category,
    ProductSubcategory subcategory,
    BigDecimal price,
    String barcode,
    List<String> images,
    Integer expirationMinMonths,
    Integer expirationMaxMonths,
    boolean requiresPrescription,
    ProductStatus status,
    List<String> contraindications,
    String dosage,
    String administration) {

  public CreateProductParams toCreateParams() {
    List<String> contraindicationsList = contraindications() != null
        ? new ArrayList<>(contraindications())
        : new ArrayList<>();

    Dosage dosageVo = requiresPrescription()
        ? Dosage.createForPrescription(dosage())
        : Dosage.create(dosage());

    var administrationVo = administration() != null
        ? Administration.create(administration())
        : null;

    var productName = name() != null ? ProductName.create(name()) : null;
    var productDescription = description() != null ? ProductDescription.create(description())
        : ProductDescription.EMPTY;
    var activeIngredientVo = activeIngredient() != null ? ActiveIngredient.create(activeIngredient()) : null;
    var productPrice = price() != null ? ProductPrice.create(price()) : null;
    var manufacturerVo = manufacturer() != null ? Manufacturer.createRequired(manufacturer()) : Manufacturer.NONE;
    var classificationVo = ProductClassification.create(type(), category(),
        subcategory() != null ? subcategory() : ProductSubcategory.NOT_SPECIFIED);
    var imagesVo = images() != null ? ProductImages.create(images()) : ProductImages.EMPTY;
    Integer minMonthsBoxed = expirationMinMonths();
    Integer maxMonthsBoxed = expirationMaxMonths();
    int minMonths = minMonthsBoxed != null ? minMonthsBoxed : 0;
    int maxMonths = maxMonthsBoxed != null ? maxMonthsBoxed : 0;
    var expirationRangeVo = (minMonths > 0 || maxMonths > 0)
        ? ExpirationRange.create(minMonths, maxMonths)
        : ExpirationRange.NOT_SPECIFIED;
    var skuVo = sku() != null && !sku().isBlank() ? SKU.create(sku()) : null;

    return CreateProductParams.builder()
        .sku(skuVo)
        .name(productName)
        .barcode(barcode())
        .description(productDescription)
        .manufacturer(manufacturerVo)
        .images(imagesVo)
        .classification(classificationVo)
        .price(productPrice)
        .expirationRange(expirationRangeVo)
        .activeIngredient(activeIngredientVo)
        .requiresPrescription(requiresPrescription())
        .status(status())
        .contraindications(contraindicationsList)
        .dosage(dosageVo)
        .administration(administrationVo)
        .build();
  }
}
