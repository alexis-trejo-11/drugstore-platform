package at.backend.drugstore.microservice.common_models.DTO.Sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class SalesSummaryDTO {

    @JsonProperty("summary_date")
    public LocalDateTime summaryDate;

    @JsonProperty("start_summary")
    public LocalDateTime startSummary;

    @JsonProperty("end_summary")
    public LocalDateTime endSummary;

    @JsonProperty("quantity_sales")
    public int quantitySales;

    @JsonProperty("total_amount_sales")
    public BigDecimal totalAmountSales;

    @JsonProperty("average_amount_sale")
    public BigDecimal averageAmountSale;

    @JsonProperty("product_summary")
    List<ProductSummaryDTO> productSummaryDTOS;
}
