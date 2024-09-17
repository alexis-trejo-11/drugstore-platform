package microservice.inventory_service.Utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProductStockStatus {
    @JsonProperty("is_product_avalaible")
    private boolean isProductAvailable;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("total_stock")
    private int totalStock;

    @JsonProperty("stock_status")
    private StockStatus stockStatus;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public enum StockStatus {
        OUT_OF_STOCK,
        LOW_STOCK,
        IN_STOCK,
        OVER_OPTIMAL_STOCK,
        OVERSTOCKED
    }
}

