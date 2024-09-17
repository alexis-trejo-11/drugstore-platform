package microservice.inventory_service.Utils;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.TransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionTypeSummary {
    @JsonProperty("transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @JsonProperty("min_product_entry")
    private TransactionOperation minProductEntry;

    @JsonProperty("max_product_entry")
    private TransactionOperation maxProductEntry;

    @JsonProperty("average_quantity_entry")
    private double averageQuantityAdded;

    @JsonProperty("most_product_present")
    private ProductStockDTO mostProductPresent;

    @JsonProperty("total_transactions")
    private int totalTransactions;
}
