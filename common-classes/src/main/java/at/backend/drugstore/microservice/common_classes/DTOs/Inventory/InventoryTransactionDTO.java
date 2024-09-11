package at.backend.drugstore.microservice.common_classes.DTOs.Inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class InventoryTransactionDTO {

    @JsonProperty("inventory_item_id")
    private Long inventoryItemId;

    @Enumerated(EnumType.STRING)
    @JsonProperty("transaction_type")
    private TransactionType transactionType;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("transaction_date")
    private LocalDateTime transactionDate;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("supplier_id")
    private Long supplierId;
}
