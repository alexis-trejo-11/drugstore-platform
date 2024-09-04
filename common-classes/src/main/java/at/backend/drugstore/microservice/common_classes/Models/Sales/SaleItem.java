package at.backend.drugstore.microservice.common_classes.Models.Sales;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
@NoArgsConstructor
public class SaleItem {
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_unit_price")
    private BigDecimal productUnitPrice;

    @Column(name = "product_quantity")
    private int productQuantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
