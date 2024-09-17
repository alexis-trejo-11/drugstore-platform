package microservice.inventory_service.Utils;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
public class TransactionSummary {
    @JsonProperty("transaction_summary_data_range")
    private String transactionSummaryDateRange;

    @JsonProperty("total_transaction_entries")
    private int totalTransactionEntries;

    @JsonProperty("transaction_type_summary")
    private List<TransactionTypeSummary> transactionTypeSummary;

    @JsonProperty("transaction_history")
    private Page<InventoryTransactionDTO> transactionHistory;

}
