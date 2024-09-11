package at.backend.drugstore.microservice.common_classes.DTOs.Sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaleItemInsertDTO {
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("productQuantity")
    private Integer quantity;
}
