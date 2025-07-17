package microservice.product_service.app.domain.port.in;

import lombok.Builder;
import lombok.Data;
import microservice.product_service.app.domain.model.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CreateProductCommand {
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
    private List<String> contraindications;
    private String dosage;
    private String administration;
}
