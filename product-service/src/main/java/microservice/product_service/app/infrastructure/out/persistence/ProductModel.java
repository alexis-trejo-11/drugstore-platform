package microservice.product_service.app.infrastructure.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.ProductCategory;
import microservice.product_service.app.domain.model.ProductId;
import microservice.product_service.app.domain.model.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "active_ingredient")
    private String activeIngredient;

    private String manufacturer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    private String barcode;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "manufacture_date")
    private LocalDateTime manufactureDate;

    @Column(name = "requires_prescription")
    private boolean requiresPrescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @ElementCollection
    @CollectionTable(name = "product_contraindications", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "contraindication")
    private List<String> contraindications;

    private String dosage;

    private String administration;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static ProductModel from(Product product) {
        return ProductModel.builder()
                .id(product.getId().value())
                .name(product.getName())
                .description(product.getDescription())
                .activeIngredient(product.getActiveIngredient())
                .manufacturer(product.getManufacturer())
                .category(product.getCategory())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .barcode(product.getBarcode())
                .batchNumber(product.getBatchNumber())
                .expirationDate(product.getExpirationDate())
                .manufactureDate(product.getManufactureDate())
                .requiresPrescription(product.isRequiresPrescription())
                .status(product.getStatus())
                .contraindications(product.getContraindications())
                .dosage(product.getDosage())
                .administration(product.getAdministration())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public Product toDomain() {
        return Product.builder()
                .id(ProductId.from(this.id))
                .name(this.name)
                .description(this.description)
                .activeIngredient(this.activeIngredient)
                .manufacturer(this.manufacturer)
                .category(this.category)
                .price(this.price)
                .stockQuantity(this.stockQuantity)
                .barcode(this.barcode)
                .batchNumber(this.batchNumber)
                .expirationDate(this.expirationDate)
                .manufactureDate(this.manufactureDate)
                .requiresPrescription(this.requiresPrescription)
                .status(this.status)
                .contraindications(this.contraindications)
                .dosage(this.dosage)
                .administration(this.administration)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}