package at.backend.drugstore.microservice.common_models.DTO.Sale;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class SaleDTO {
    private Long id;

    private LocalDateTime saleDate;

    private BigDecimal subTotal;

    private BigDecimal discount;

    private BigDecimal total;

    private Long employeeId;

    private Long clientId;

    private String saleStatus;

    private List<SaleItemDTO> saleItemDTOS;

    public List<Long> getProductsIds() {
        return saleItemDTOS.stream()
                .map(SaleItemDTO::getProductId)
                .collect(Collectors.toList());
    }
}
