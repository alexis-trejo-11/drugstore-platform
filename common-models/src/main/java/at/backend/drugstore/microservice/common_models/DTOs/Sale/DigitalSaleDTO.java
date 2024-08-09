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
@Schema(description = "Data Transfer Object for Digital Sale")
public class DigitalSaleDTO {

    @Schema(description = "Unique identifier of the sale")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "Date and time when the sale was made")
    @JsonProperty("sale_date")
    private LocalDateTime saleDate;

    @Schema(description = "Subtotal amount of the sale before discount")
    @JsonProperty("subtotal")
    private BigDecimal subtotal;

    @Schema(description = "Discount amount applied to the sale")
    @JsonProperty("discount")
    private BigDecimal discount;

    @Schema(description = "Total amount of the sale after discount")
    @JsonProperty("total")
    private BigDecimal total;

    @Schema(description = "Identifier of the associated order")
    @JsonProperty("order_id")
    private Long orderId;

    @Schema(description = "Identifier of the associated payment")
    @JsonProperty("payment_id")
    private Long paymentId;

    @Schema(description = "Identifier of the client who made the purchase")
    @JsonProperty("client_id")
    private Long clientId;

    @Schema(description = "Current status of the sale")
    @JsonProperty("sale_status")
    private String saleStatus;

    @Schema(description = "List of items included in the sale")
    @JsonProperty("sale_items")
    private List<SaleItemDTO> saleItemDTOS;
}