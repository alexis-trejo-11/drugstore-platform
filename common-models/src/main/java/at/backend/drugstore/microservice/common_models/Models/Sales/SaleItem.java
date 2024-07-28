package at.backend.drugstore.microservice.common_models.Models.Sales;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
@NoArgsConstructor
public class SaleItem {
    private Long productId;
    private String productName;
    private BigDecimal productUnitPrice;
    private int productQuantity;
    private LocalDateTime createdAt;

}
