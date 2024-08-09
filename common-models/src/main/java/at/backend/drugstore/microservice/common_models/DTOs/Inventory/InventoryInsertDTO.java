package at.backend.drugstore.microservice.common_models.DTOs.Inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Date;

@Data
@NoArgsConstructor
public class InventoryInsertDTO {

    @NotBlank(message = "Batch number is required")
    @JsonProperty("batch_number")
    private String batchNumber;

    @NotNull(message = "quantity is obligatory")
    private int quantity;

    @Future(message = "Expiration date must be in the future")
    @NotNull(message = "Expiration date is required")
    @JsonProperty("expiration_date")
    private Date expirationDate;

    @NotBlank(message = "Location is required")
    private String location;

    @PastOrPresent(message = "Date received must be in the past or present")
    @NotNull(message = "Date received is required")
    @JsonProperty("date_received")
    private Date dateReceived;

    @NotNull(message = "product_id is required")
    @JsonProperty("product_id")
    private Long productId;

    @Valid
    @NotNull(message = "Inventory transaction details are required")
    @JsonProperty("inventory_transaction")
    private InventoryTransactionInsertDTO inventoryTransactionInsertDTO;

}
