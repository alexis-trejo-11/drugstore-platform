package at.backend.drugstore.microservice.common_models.Models.Sales;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
@NoArgsConstructor
public class SaleItem {

    private BigDecimal productUnitPrice;

    private int productQuantity;

    private Long productId;

    private LocalDateTime createdAt;

    private String productName;

}
