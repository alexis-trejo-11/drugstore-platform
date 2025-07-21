package microservice.product_service.app.infrastructure.in.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PastOrPresent;
import microservice.product_service.app.domain.model.ProductCategory;
import microservice.product_service.app.domain.model.ProductId;
import microservice.product_service.app.domain.port.in.command.UpdateProductCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 255, message = "Active ingredient must not exceed 255 characters")
    private String activeIngredient;

    @Size(max = 255, message = "Manufacturer must not exceed 255 characters")
    private String manufacturer;

    private ProductCategory category;

    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    private String barcode;

    @Size(max = 50, message = "Batch number must not exceed 50 characters")
    private String batchNumber;

    @FutureOrPresent(message = "Expiration date must be in the present or future") // Changed from Future
    private LocalDateTime expirationDate;

    @PastOrPresent(message = "Manufacture date cannot be in the future")
    private LocalDateTime manufactureDate;

    // Boolean fields are generally optional without specific annotations if not primitives
    private Boolean requiresPrescription; // Use Boolean wrapper for nullability

    private List<String> contraindications; // Can be null, or empty list

    @Size(max = 255, message = "Dosage must not exceed 255 characters")
    private String dosage;

    @Size(max = 500, message = "Administration must not exceed 500 characters")
    private String administration;


    public UpdateProductCommand toCommand(ProductId productId) {
        return UpdateProductCommand.builder()
                .productId(productId)
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
                .requiresPrescription(this.getRequiresPrescription() != null ? this.getRequiresPrescription() : false)
                .contraindications(this.getContraindications())
                .dosage(this.getDosage())
                .administration(this.getAdministration())
                .build();
    }
    
}
