package at.backend.drugstore.microservice.common_classes.DTOs.Inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class InventoryTransactionInsertDTO {
    @JsonProperty("inventory_item_id")
    @NotNull(message = "inventory_item_id is obligatory")
    @Positive(message = "inventory_item_id must be positive")
    private Long inventoryItemId;

    // Accepted Enums -> RECEIVED, SOLD, ADJUSTED, RETURNED, DAMAGED, EXPIRED
    @Enumerated(EnumType.ORDINAL)
    @JsonProperty("transaction_type")
    @NotNull(message = "transaction_type is obligatory")
    private TransactionType transactionType;

    @JsonProperty("quantity")
    @NotNull(message = "quantity is obligatory")
    private int quantity;

    @JsonProperty("transaction_date")
    @NotNull(message = "transaction_date is obligatory")
    private LocalDateTime transactionDate;

    @JsonProperty("expiration_date")
    @NotNull(message = "expiration_date is obligatory")
    @Future(message = "expiration_date must be setted on the future")
    private LocalDateTime expirationDate;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("supplier_id")
    @NotNull(message = "supplier_id is obligatory")
    @Positive(message = "supplier_id must be positive")
    private Long supplierId;

    @JsonProperty("employee_id")
    @NotNull(message = "employee_id is obligatory")
    @Positive(message = "employee_id must be positive")
    private Long employeeId;
}

