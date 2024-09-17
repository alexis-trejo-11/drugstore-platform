package microservice.inventory_service.Utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class InventorySummary {
    @JsonProperty("summary_date_creation")
    LocalDateTime summaryDateCreation;

    @JsonProperty("total_unique_items")
    int totalUniqueItems;

    @JsonProperty("total_item_entries")
    int totalItemEntries;

    @JsonProperty("low_stock_items_count")
    int lowStockItemsCount;

    @JsonProperty("expired_items_count")
    int expiredItemsCount;

    @JsonProperty("near_expiry_items_count")
    int nearExpiryItemsCount;

    @JsonProperty("inventory_items_added_this_month")
    int itemsRecentlyAdded;

    @JsonProperty("total_inventory_stock")
    List<ProductStockDTO> productStockDTO;
}
