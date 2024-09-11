package microservice.inventory_service.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventorySummary {
    @JsonProperty("total_unique_items")
    int totalUniqueItems;

    @JsonProperty("total_item_quantity")
    int totalItemQuantity;

    @JsonProperty("low_stock_items_count")
    int lowStockItemsCount;

    @JsonProperty("expired_items_count")
    int expiredItemsCount;

    @JsonProperty("near_expiry_items_count")
    int nearExpiryItemsCount;

    @JsonProperty("total_inventory_value")
    double totalInventoryValue;
}
