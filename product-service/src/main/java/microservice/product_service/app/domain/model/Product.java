package microservice.product_service.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.product_service.app.domain.exception.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private static final BigDecimal MAX_PRICE_USD = new BigDecimal("1000.00");
    private static final BigDecimal MIN_PRICE_USD = new BigDecimal("1.00");
    private static final int MAX_STOCK = 10000;
    private static final int MIN_STOCK = 0;
    private static final int MIN_BARCODE_LENGTH = 8;
    private static final int MAX_BARCODE_LENGTH = 20;
    private static final int MIN_BATCH_NUMBER_LENGTH = 3;
    private static final int MAX_BATCH_NUMBER_LENGTH = 15;

    private ProductId id;
    private String name;
    private String description;
    private String activeIngredient;
    private String manufacturer;
    private ProductCategory category;
    private BigDecimal price;
    private Integer stockQuantity;
    private String barcode;
    private String batchNumber;
    private LocalDateTime expirationDate;
    private LocalDateTime manufactureDate;
    private boolean requiresPrescription;
    private ProductStatus status;
    private List<String> contraindications;
    private String dosage;
    private String administration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void validateProduct() {
        validatePrice();
        validateStock();
        validateBarcode();
        validateBatchNumber();
        validateDates();
        validateName();
        validateActiveIngredient();
    }

    public void validatePrice() {
        if (price == null) {
            throw new InvalidPriceException("Product price cannot be null");
        }
        if (price.compareTo(MIN_PRICE_USD) < 0 || price.compareTo(MAX_PRICE_USD) > 0) {
            throw new InvalidPriceException(
                    String.format("Price must be between %s and %s USD", MIN_PRICE_USD, MAX_PRICE_USD)
            );
        }
    }

    public void validateStock() {
        if (stockQuantity == null) {
            throw new InvalidStockException("Stock quantity cannot be null");
        }
        if (stockQuantity < MIN_STOCK) {
            throw new InvalidStockException("Stock quantity cannot be negative");
        }
        if (stockQuantity > MAX_STOCK) {
            throw new InvalidStockException(
                    String.format("Stock quantity cannot exceed %d units", MAX_STOCK)
            );
        }
    }

    public void validateBarcode() {
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

    public void validateBatchNumber() {
        if (batchNumber == null || batchNumber.trim().isEmpty()) {
            throw new ProductValidationException("Batch number cannot be null or empty");
        }
        if (batchNumber.length() < MIN_BATCH_NUMBER_LENGTH ||
                batchNumber.length() > MAX_BATCH_NUMBER_LENGTH) {
            throw new ProductValidationException(
                    String.format("Batch number must be between %d and %d characters",
                            MIN_BATCH_NUMBER_LENGTH, MAX_BATCH_NUMBER_LENGTH)
            );
        }
    }

    public void validateDates() {
        LocalDateTime now = LocalDateTime.now();

        if (manufactureDate == null) {
            throw new InvalidManufactureDateException("Manufacture date cannot be null");
        }
        if (expirationDate == null) {
            throw new InvalidExpirationDateException("Expiration date cannot be null");
        }
        if (manufactureDate.isAfter(now)) {
            throw new InvalidManufactureDateException("Manufacture date cannot be in the future");
        }
        if (expirationDate.isBefore(manufactureDate)) {
            throw new InvalidExpirationDateException(
                    "Expiration date must be after manufacture date"
            );
        }
        if (expirationDate.isBefore(now)) {
            throw new ExpiredProductException("Product is already expired");
        }
    }

    public void validateName() {
        if (name == null || name.trim().isEmpty()) {
            throw new ProductValidationException("Product name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new ProductValidationException("Product name cannot exceed 100 characters");
        }
    }

    public void validateActiveIngredient() {
        if (activeIngredient == null || activeIngredient.trim().isEmpty()) {
            throw new ProductValidationException("Active ingredient cannot be null or empty");
        }
        if (activeIngredient.length() > 150) {
            throw new ProductValidationException("Active ingredient cannot exceed 150 characters");
        }
    }

    public void checkPrescriptionRequirement() {
        if (requiresPrescription && (dosage == null || dosage.isEmpty())) {
            throw new PrescriptionRequiredException(
                    "Dosage information is required for prescription products"
            );
        }
    }

    // Métodos existentes mejorados

    public void updateStock(int quantity) {
        if (quantity < MIN_STOCK) {
            throw new InvalidStockException(
                    String.format("Stock quantity cannot be less than %d", MIN_STOCK)
            );
        }
        if (quantity > MAX_STOCK) {
            throw new InvalidStockException(
                    String.format("Stock quantity cannot exceed %d", MAX_STOCK)
            );
        }
        this.stockQuantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidStockException("Reduction quantity must be positive");
        }
        if (quantity > this.stockQuantity) {
            throw new InvalidStockException("Insufficient stock");
        }
        this.stockQuantity -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationDate);
    }

    public void checkExpirationBeforeSale() {
        if (isExpired()) {
            throw new ExpiredProductException(
                    String.format("Product expired on %s", expirationDate)
            );
        }
    }
}