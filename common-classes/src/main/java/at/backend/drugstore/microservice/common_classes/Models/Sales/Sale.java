package at.backend.drugstore.microservice.common_classes.Models.Sales;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor
@Data
public class Sale {

    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    @Column(name = "subtotal")
    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal total;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "sale_status")
    @Enumerated(EnumType.STRING)
    private SaleStatus saleStatus;
}
