package at.backend.drugstore.microservice.common_classes.DTOs.Sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateSaleDTO {
    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("cashier_id")
    private Long cashierId;

    @JsonProperty("cashier_name")
    private String cashierName;

    @JsonProperty("sale_id")
    private Long saleId;

    @JsonProperty("sale_date")
    private LocalDateTime saleDate;

    @JsonProperty("sale_status")
    private String saleStatus;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("items")
    private List<SaleItemDTO> items;

}
