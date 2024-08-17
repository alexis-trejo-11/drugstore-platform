package at.backend.drugstore.microservice.common_classes.Models.Sales;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;


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
