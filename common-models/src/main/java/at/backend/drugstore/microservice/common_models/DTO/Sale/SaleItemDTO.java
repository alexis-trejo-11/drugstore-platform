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
        private int productQuantity;

    @JsonProperty("product_unit_price")
    private BigDecimal productUnitPrice;

    @JsonProperty("item_total")
    private BigDecimal itemTotal;
}
