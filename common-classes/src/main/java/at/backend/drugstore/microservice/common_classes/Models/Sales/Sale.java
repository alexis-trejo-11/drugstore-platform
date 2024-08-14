package at.backend.drugstore.microservice.common_classes.Models.Sales;

import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor
@Data
public class Sale {

    private LocalDateTime saleDate;

    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal total;

    private Long clientId;

    @Enumerated(EnumType.STRING)
    private SaleStatus saleStatus;


}
