package at.backend.drugstore.microservice.common_models.DTO.Sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SaleItemDTO {
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("sub_total")
    private BigDecimal subtotal;
}
