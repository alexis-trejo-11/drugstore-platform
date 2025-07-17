package microservice.product_service.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
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


    public void updateStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.stockQuantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void reduceStock(int quantity) {
        if (quantity > this.stockQuantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.stockQuantity -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationDate);
    }

}
