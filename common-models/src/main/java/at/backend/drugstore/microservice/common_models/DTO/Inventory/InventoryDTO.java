package at.backend.drugstore.microservice.common_models.DTO.Inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class InventoryDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("batch_number")
    private String batchNumber;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("expiration_date")
    private Date expirationDate;

    @JsonProperty("location")
    private String location;

    @JsonProperty("date_received")
    private Date dateReceived;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("inventory_transactions")
    private List<InventoryTransactionDTO> inventoryTransactionDTOS;

}
