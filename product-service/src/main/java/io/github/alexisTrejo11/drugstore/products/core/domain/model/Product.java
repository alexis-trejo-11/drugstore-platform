package io.github.alexisTrejo11.drugstore.products.core.domain.model;

import java.math.BigDecimal;
import java.util.List;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.InvalidPriceException;
import io.github.alexisTrejo11.drugstore.products.core.domain.exception.PrescriptionRequiredException;
import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValidationException;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.enums.ProductStatus;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ActiveIngredient;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.Administration;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.Dosage;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ExpirationRange;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.Manufacturer;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductClassification;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductDescription;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductID;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductImages;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductName;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductPrice;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductTimeStamps;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.SKU;
import io.github.alexisTrejo11.drugstore.products.core.domain.validation.ProductValidation;

/**
 * Product aggregate root - represents catalog product data.
 * Stock-related data (manufacture dates, expiration dates per batch,
 * quantities)
 * are managed by inventory-service.
 */
public class Product {
  private static final BigDecimal MAX_PRICE_USD = new BigDecimal("10000.00");
  private static final BigDecimal MIN_PRICE_USD = new BigDecimal("0.01");
  private static final int MIN_BARCODE_LENGTH = 8;
  private static final int MAX_BARCODE_LENGTH = 20;

  private ProductID id;
  private SKU sku;
  private String barcode;

  private ProductName name;
  private ProductDescription description;
  private Manufacturer manufacturer;
  private ProductImages images;

  private ProductClassification classification;
  private ProductPrice price;
  private ExpirationRange expirationRange;

  private ActiveIngredient activeIngredient;
  private boolean requiresPrescription;
  private List<String> contraindications;
  private Dosage dosage;
  private Administration administration;

  private ProductStatus status;
  private ProductTimeStamps timeStamps;

  private Product() {
    this.sku = SKU.NONE;
    this.barcode = "";
    this.images = ProductImages.EMPTY;
    this.classification = ProductClassification.NOT_CLASSIFIED;
    this.expirationRange = ExpirationRange.NOT_SPECIFIED;
    this.status = ProductStatus.UKNOWN;
    this.timeStamps = ProductTimeStamps.now();
  }

  public static Product create(CreateProductParams params) {
    ProductValidation.requireNonNull(params, "CreateProductParams");
    ProductValidation.requireNonNull(params.name(), "Product name");
    ProductValidation.requireNonNull(params.classification(), "Product classification");

    Product product = new Product();

    // Generate SKU based on product type if not provided
    if (params.sku() != null && !params.sku().isEmpty()) {
      product.sku = params.sku();
    } else {
      product.sku = SKU.generate(params.classification().getSkuPrefix());
    }

    product.id = ProductID.generate();
    product.barcode = params.barcode();
    product.name = params.name();
    product.description = params.description() != null ? params.description() : ProductDescription.EMPTY;
    product.manufacturer = params.manufacturer() != null ? params.manufacturer() : Manufacturer.NONE;
    product.images = params.images() != null ? params.images() : ProductImages.EMPTY;
    product.classification = params.classification();
    product.price = params.price();
    product.expirationRange = params.expirationRange() != null ? params.expirationRange()
        : ExpirationRange.NOT_SPECIFIED;
    product.activeIngredient = params.activeIngredient();
    product.requiresPrescription = params.requiresPrescription();
    product.contraindications = params.contraindications();
    product.dosage = params.dosage();
    product.administration = params.administration();
    product.status = params.status() != null ? params.status() : ProductStatus.INACTIVE;

    product.validateProduct();
    product.checkPrescriptionRequirement();

    return product;
  }

  public static Product reconstruct(ReconstructProductParams params) {
    Product product = new Product();
    product.id = params.id();
    product.sku = params.sku();
    product.barcode = params.barcode();
    product.name = params.name();
    product.description = params.description();
    product.manufacturer = params.manufacturer();
    product.images = params.images() != null ? params.images() : ProductImages.EMPTY;
    product.classification = params.classification();
    product.price = params.price();
    product.expirationRange = params.expirationRange() != null ? params.expirationRange()
        : ExpirationRange.NOT_SPECIFIED;
    product.activeIngredient = params.activeIngredient();
    product.requiresPrescription = params.requiresPrescription();
    product.contraindications = params.contraindications();
    product.dosage = params.dosage();
    product.administration = params.administration();
    product.status = params.status();
    product.timeStamps = params.timeStamps();

    return product;
  }

  public void activate() {
    if (this.status == ProductStatus.ACTIVE) {
      throw new ProductValidationException("Product is already active");
    }
    this.status = ProductStatus.ACTIVE;
    this.timeStamps.markAsUpdated();
  }

  public void deactivate() {
    if (this.status == ProductStatus.INACTIVE) {
      throw new ProductValidationException("Product is already inactive");
    }
    this.status = ProductStatus.INACTIVE;
    this.timeStamps.markAsUpdated();
  }

  public void updateInformation(
      ProductName name,
      String barcode,
      SKU sku,
      ProductDescription description,
      Manufacturer manufacturer,
      ProductClassification classification,
      ProductImages images) {

    this.name = name != null ? name : this.name;
    this.barcode = barcode != null ? barcode : this.barcode;
    this.sku = sku != null ? sku : this.sku;
    this.description = description != null ? description : this.description;
    this.manufacturer = manufacturer != null ? manufacturer : this.manufacturer;
    this.classification = classification != null ? classification : this.classification;
    this.images = images != null ? images : this.images;

    validateName();
    validateDescription();

    this.timeStamps.markAsUpdated();
  }

  public void updatePricing(ProductPrice newPrice) {
    ProductValidation.requireNonNull(newPrice, "ProductPrice");
    validatePrice(newPrice);

    this.price = newPrice;
    this.timeStamps.markAsUpdated();
  }

  public void updateMedicalInfo(ActiveIngredient activeIngredient,
      List<String> contraindications,
      Dosage dosage,
      Administration administration,
      Boolean requiresPrescription) {

    this.activeIngredient = activeIngredient != null ? activeIngredient : this.activeIngredient;
    this.contraindications = contraindications != null ? contraindications : this.contraindications;
    this.dosage = dosage != null ? dosage : this.dosage;
    this.administration = administration != null ? administration : this.administration;
    this.requiresPrescription = requiresPrescription != null ? requiresPrescription : this.requiresPrescription;

    checkPrescriptionRequirement();

    this.timeStamps.markAsUpdated();
  }

  public void updateImages(ProductImages images) {
    this.images = images != null ? images : ProductImages.EMPTY;
    this.timeStamps.markAsUpdated();
  }

  public void addImage(String imageUrl) {
    this.images = this.images.addImage(imageUrl);
    this.timeStamps.markAsUpdated();
  }

  public void removeImage(String imageUrl) {
    this.images = this.images.removeImage(imageUrl);
    this.timeStamps.markAsUpdated();
  }

  public void updateExpirationRange(ExpirationRange range) {
    this.expirationRange = range != null ? range : ExpirationRange.NOT_SPECIFIED;
    this.timeStamps.markAsUpdated();
  }

  private void validateProduct() {
    validateBarcode();
    validateName();
    validatePrice(this.price);
  }

  private void validateBarcode() {
    if (barcode == null || barcode.trim().isEmpty()) {
      return; // Barcode is optional
    }
    if (barcode.length() < MIN_BARCODE_LENGTH || barcode.length() > MAX_BARCODE_LENGTH) {
      throw new ProductValidationException(
          String.format("Barcode must be between %d and %d characters",
              MIN_BARCODE_LENGTH, MAX_BARCODE_LENGTH));
    }
    if (!barcode.matches("[0-9]+")) {
      throw new ProductValidationException("Barcode must contain only digits");
    }
  }

  private void validateName() {
    if (this.name == null) {
      throw new ProductValidationException("Product name cannot be null");
    }
  }

  private void validateDescription() {
    if (this.description != null && this.description.getValue().length() > 2000) {
      throw new ProductValidationException("Product description cannot exceed 2000 characters");
    }
  }

  private void validatePrice(ProductPrice price) {
    if (price == null) {
      throw new InvalidPriceException("Product price cannot be null");
    }

    BigDecimal value = price.value();
    if (value.compareTo(MIN_PRICE_USD) < 0 || value.compareTo(MAX_PRICE_USD) > 0) {
      throw new InvalidPriceException(
          String.format("Price must be between %s and %s USD", MIN_PRICE_USD, MAX_PRICE_USD));
    }
  }

  private void checkPrescriptionRequirement() {
    if (requiresPrescription && (dosage == null || dosage.getValue().isEmpty())) {
      throw new PrescriptionRequiredException(
          "Dosage information is required for prescription products");
    }
  }

  public boolean isDeleted() {
    return this.timeStamps != null && this.timeStamps.getDeletedAt() != null;
  }

  public void softDelete() {
    if (this.timeStamps == null) {
      throw new IllegalStateException("Product timestamps are not initialized");
    }
    this.timeStamps.markAsDeleted();
    this.timeStamps.markAsUpdated();
  }

  public void restore() {
    if (this.timeStamps == null) {
      throw new IllegalStateException("Product timestamps are not initialized");
    }
    this.timeStamps.restore();
    this.timeStamps.markAsUpdated();
  }

  // Getters
  public ProductID getId() {
    return id;
  }

  public SKU getSku() {
    return sku;
  }

  public String getBarcode() {
    return barcode;
  }

  public ProductName getName() {
    return name;
  }

  public ProductDescription getDescription() {
    return description;
  }

  public Manufacturer getManufacturer() {
    return manufacturer;
  }

  public ProductImages getImages() {
    return images;
  }

  public ProductClassification getClassification() {
    return classification;
  }

  public ProductPrice getPrice() {
    return price;
  }

  public ExpirationRange getExpirationRange() {
    return expirationRange;
  }

  public ActiveIngredient getActiveIngredient() {
    return activeIngredient;
  }

  public boolean isRequiresPrescription() {
    return requiresPrescription;
  }

  public List<String> getContraindications() {
    return contraindications;
  }

  public Dosage getDosage() {
    return dosage;
  }

  public Administration getAdministration() {
    return administration;
  }

  public ProductStatus getStatus() {
    return status;
  }

  public ProductTimeStamps getTimeStamps() {
    return timeStamps;
  }
}
