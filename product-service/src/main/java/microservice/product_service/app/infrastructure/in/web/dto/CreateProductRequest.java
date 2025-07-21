package microservice.product_service.app.infrastructure.in.web.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import microservice.product_service.app.domain.model.ProductCategory;
import microservice.product_service.app.domain.port.in.command.CreateProductCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 255, message = "Active ingredient must not exceed 255 characters")
    private String activeIngredient;

    @Size(max = 255, message = "Manufacturer must not exceed 255 characters")
    private String manufacturer;

    @NotNull(message = "Category is required")
    private ProductCategory category;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    private String barcode;

    @Size(max = 50, message = "Batch number must not exceed 50 characters")
    private String batchNumber;

    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expirationDate;

    @PastOrPresent(message = "Manufacture date cannot be in the future")
    private LocalDateTime manufactureDate;

    private boolean requiresPrescription;

    private List<String> contraindications;

    @Size(max = 255, message = "Dosage must not exceed 255 characters")
    private String dosage;

    @Size(max = 500, message = "Administration must not exceed 500 characters")
    private String administration;
    
    
    public CreateProductCommand toCommand() {
        return CreateProductCommand.builder()
                .name(this.getName())
                .description(this.getDescription())
                .activeIngredient(this.getActiveIngredient())
                .manufacturer(this.getManufacturer())
                .category(this.getCategory())
                .price(this.getPrice())
                .stockQuantity(this.getStockQuantity())
                .barcode(this.getBarcode())
                .batchNumber(this.getBatchNumber())
                .expirationDate(this.getExpirationDate())
                .manufactureDate(this.getManufactureDate())
                .requiresPrescription(this.isRequiresPrescription())
                .contraindications(this.getContraindications())
                .dosage(this.getDosage())
                .administration(this.getAdministration())
                .build();
    }
}