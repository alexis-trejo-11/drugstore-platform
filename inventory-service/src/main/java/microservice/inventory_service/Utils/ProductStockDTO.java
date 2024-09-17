package microservice.inventory_service.Utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class ProductStockDTO {
    @JsonProperty("product_id")
    private Long product_id;

    @JsonProperty("current_total_stock")
    private int currentTotalStock;

    public ProductStockDTO(Long product_id, Long currentTotalStock) {
        this.product_id = product_id;
        this.currentTotalStock = currentTotalStock != null ? currentTotalStock.intValue() : 0;
    }
}
