package at.backend.drugstore.microservice.common_models.DTOs.Sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Summary of sales for a given period")
public class SalesSummaryDTO {

    @Schema(description = "The date of the summary", example = "2024-08-10T15:00:00")
    @JsonProperty("summary_date")
    private LocalDateTime summaryDate;

    @Schema(description = "The start date of the summary period", example = "2024-08-01T00:00:00")
    @JsonProperty("start_summary")
    private LocalDateTime startSummary;

    @Schema(description = "The end date of the summary period", example = "2024-08-31T23:59:59")
    @JsonProperty("end_summary")
    private LocalDateTime endSummary;

    @Schema(description = "Total number of sales", example = "150")
    @JsonProperty("quantity_sales")
    private int quantitySales;

    @Schema(description = "Total amount of sales", example = "12000.50")
    @JsonProperty("total_amount_sales")
    private BigDecimal totalAmountSales;

    @Schema(description = "Average amount per sale", example = "80.00")
    @JsonProperty("average_amount_sale")
    private BigDecimal averageAmountSale;

    @Schema(description = "List of product summaries")
    @JsonProperty("product_summary")
    private List<ProductSummaryDTO> productSummaryDTOS;
}
