package microservice.product_service.app.infrastructure.in.web.dto;

import lombok.Builder;
import lombok.Data;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private String id;
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

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId().toString())
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


}