package microservice.product_service.app.domain.model;

import microservice.product_service.app.domain.exception.*;
import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.enums.ProductStatus;
import microservice.product_service.app.domain.model.valueobjects.*;
import microservice.product_service.app.domain.validation.ProductValidation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class Product {
	private static final BigDecimal MAX_PRICE_USD = new BigDecimal("1000.00");
	private static final BigDecimal MIN_PRICE_USD = new BigDecimal("1.00");
	private static final int MIN_BARCODE_LENGTH = 8;
	private static final int MAX_BARCODE_LENGTH = 20;

	private ProductID id;
	private ProductCode code;
	private String barcode;
	private boolean requiresPrescription;
	private ProductStatus status;
	private ProductName name;
	private ProductDescription description;
	private ActiveIngredient activeIngredient;
	private Manufacturer manufacturer;
	private ProductCategory category;
	private ProductPrice price;
	private ProductDates dates;
	private Set<String> contraindications;
	private Dosage dosage;
	private Administration administration;
	private ProductTimeStamps timeStamps;

	private Product() {
		this.id = null;
		this.code = ProductCode.NONE;
		this.barcode = "";
		this.status = ProductStatus.UKNOWN;
		this.timeStamps = ProductTimeStamps.now();
	}

	public static Product create(CreateProductParams params) {
		ProductValidation.requireNonNull(params, "CreateProductParams");
		ProductValidation.requireNonNull(params.code(), "Product code");
		ProductValidation.requireNonNull(params.name(), "Product name");
		ProductValidation.requireNonNull(params.description(), "Product description");
		ProductValidation.requireNonNull(params.manufacturer(), "Manufacturer");
		ProductValidation.requireNonNull(params.category(), "Product category");

		Product product = new Product();
		product.id = ProductID.generate();
		product.code = params.code();
		product.name = params.name();
		product.description = params.description();
		product.activeIngredient = params.activeIngredient();
		product.manufacturer = params.manufacturer();
		product.category = params.category();
		product.price = params.price();
		product.barcode = params.barcode();
		product.dates = params.dates();
		product.requiresPrescription = params.requiresPrescription();
		product.status = product.status == null ? ProductStatus.INACTIVE : params.status();
		product.contraindications = params.contraindications();
		product.dosage = params.dosage();
		product.administration = params.administration();

		product.validateProduct();
		product.checkPrescriptionRequirement();

		return product;
	}

	public static Product reconstruct(ReconstructProductParams params) {
		ProductValidation.requireNonNull(params, "ReconstructProductParams");
		ProductValidation.requireNonNull(params.id(), "Product ID");
		ProductValidation.requireNonNull(params.code(), "Product code");
		ProductValidation.requireNonNull(params.name(), "Product name");
		ProductValidation.requireNonNull(params.status(), "Product status");

		Product product = new Product();
		product.id = params.id();
		product.code = params.code();
		product.name = params.name();
		product.description = params.description();
		product.activeIngredient = params.activeIngredient();
		product.manufacturer = params.manufacturer();
		product.category = params.category();
		product.price = params.price();
		product.barcode = params.barcode();
		product.dates = params.dates();
		product.requiresPrescription = params.requiresPrescription();
		product.status = params.status();
		product.contraindications = params.contraindications();
		product.dosage = params.dosage();
		product.administration = params.administration();
		product.timeStamps = params.timeStamps();

		return product;
	}

	public void activate() {
		if (this.status == ProductStatus.ACTIVE) {
			throw new ProductValidationException("Product is already active");
		}

		if (isExpired()) {
			throw new ExpiredProductException("Cannot activate an expired product");
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

	public void updateInformation(ProductName name, ProductDescription description,
	                              Manufacturer manufacturer, ProductCategory category) {
		ProductValidation.requireNonNull(name, "ProductName");
		ProductValidation.requireNonNull(description, "ProductDescription");
		ProductValidation.requireNonNull(manufacturer, "Manufacturer");
		ProductValidation.requireNonNull(category, "ProductCategory");

		this.name = name;
		this.description = description;
		this.manufacturer = manufacturer;
		this.category = category;

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
	                              Set<String> contraindications,
	                              Dosage dosage,
	                              Administration administration,
	                              boolean requiresPrescription) {
		ProductValidation.requireNonNull(activeIngredient, "ActiveIngredient");
		ProductValidation.requireNonNull(contraindications, "Contraindications");

		this.activeIngredient = activeIngredient;
		this.contraindications = contraindications;
		this.dosage = dosage;
		this.administration = administration;
		this.requiresPrescription = requiresPrescription;

		checkPrescriptionRequirement();
		validateActiveIngredient();

		this.timeStamps.markAsUpdated();
	}

	private void validateProduct() {
		validateBarcode();
		validateDates();
		validateName();
		validateActiveIngredient();
		validatePrice(this.price);
	}

	private void validateBarcode() {
		if (barcode == null || barcode.trim().isEmpty()) {
			throw new ProductValidationException("Barcode cannot be null or empty");
		}
		if (barcode.length() < MIN_BARCODE_LENGTH || barcode.length() > MAX_BARCODE_LENGTH) {
			throw new ProductValidationException(
					String.format("Barcode must be between %d and %d characters",
							MIN_BARCODE_LENGTH, MAX_BARCODE_LENGTH)
			);
		}
		if (!barcode.matches("[0-9]+")) {
			throw new ProductValidationException("Barcode must contain only digits");
		}
	}

	private void validateDates() {
		ProductDates dates = this.dates;
		if (dates == null) {
			throw new ProductValidationException("Product dates cannot be null");
		}

		LocalDateTime now = LocalDateTime.now();

		if (dates.manufactureDate() == null) {
			throw new InvalidManufactureDateException("Manufacture date cannot be null");
		}
		if (dates.expirationDate() == null) {
			throw new InvalidExpirationDateException("Expiration date cannot be null");
		}
		if (dates.manufactureDate().isAfter(now)) {
			throw new InvalidManufactureDateException("Manufacture date cannot be in the future");
		}
		if (dates.expirationDate().isBefore(dates.manufactureDate())) {
			throw new InvalidExpirationDateException(
					"Expiration date must be after manufacture date"
			);
		}
	}

	private void validateName() {
		if (this.name == null) {
			throw new ProductValidationException("Product name cannot be null");
		}
	}

	private void validateDescription() {
		if (this.description != null && this.description.getValue().length() > 1000) {
			throw new ProductValidationException("Product description cannot exceed 1000 characters");
		}
	}

	private void validateActiveIngredient() {
		if (this.activeIngredient == null) {
			throw new ProductValidationException("Active ingredient cannot be null");
		}
	}

	private void validatePrice(ProductPrice price) {
		if (price == null) {
			throw new InvalidPriceException("Product price cannot be null");
		}

		BigDecimal value = price.value();
		if (value.compareTo(MIN_PRICE_USD) < 0 || value.compareTo(MAX_PRICE_USD) > 0) {
			throw new InvalidPriceException(
					String.format("Price must be between %s and %s USD", MIN_PRICE_USD, MAX_PRICE_USD)
			);
		}
	}


	private void checkPrescriptionRequirement() {
		if (requiresPrescription && (dosage == null || dosage.getValue().isEmpty())) {
			throw new PrescriptionRequiredException(
					"Dosage information is required for prescription products"
			);
		}
	}

	public boolean isExpired() {
		return this.dates != null &&
				this.dates.expirationDate().isBefore(LocalDateTime.now());
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

	public ProductID getId() {
		return id;
	}

	public ProductCode getCode() {
		return code;
	}

	public String getBarcode() {
		return barcode;
	}

	public boolean isRequiresPrescription() {
		return requiresPrescription;
	}

	public ProductStatus getStatus() {
		return status;
	}

	public ProductName getName() {
		return name;
	}

	public ProductDescription getDescription() {
		return description;
	}

	public ActiveIngredient getActiveIngredient() {
		return activeIngredient;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public ProductPrice getPrice() {
		return price;
	}

	public ProductDates getDates() {
		return dates;
	}

	public Set<String> getContraindications() {
		return contraindications;
	}

	public Dosage getDosage() {
		return dosage;
	}

	public Administration getAdministration() {
		return administration;
	}

	public ProductTimeStamps getTimeStamps() {
		return timeStamps;
	}
}