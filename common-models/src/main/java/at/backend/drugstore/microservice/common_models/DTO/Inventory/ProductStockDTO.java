package at.backend.drugstore.microservice.common_models.DTO.Inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductStockDTO {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("total_stock")
    private int totalStock;

    @JsonProperty("inventories")
    public List<InventoryStockDTO> inventoryStockDTOS;


}
