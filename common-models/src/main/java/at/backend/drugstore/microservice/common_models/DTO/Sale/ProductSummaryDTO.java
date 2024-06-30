package at.backend.drugstore.microservice.common_models.DTO.Sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductSummaryDTO {

    @JsonProperty("product_id")
    public Long productId;

    @JsonProperty("product_name")
    public String productName;

    @JsonProperty("quantity")
    public int quantity;

    @JsonProperty("unit_price")
    public BigDecimal unitPrice;

    @JsonProperty("total")
    public BigDecimal total;


}
