package at.backend.drugstore.microservice.common_models.DTO.Sale;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class DigitalSaleDTO {
    private Long id;

    private LocalDateTime saleDate;

    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal total;

    private Long orderId;

    private Long paymentId;

    private Long clientId;

    private String saleStatus;

    private List<SaleItemDTO> saleItemDTOS;

}
