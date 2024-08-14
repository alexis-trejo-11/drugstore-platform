package at.backend.drugstore.microservice.common_classes.DTOs.Inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class InventoryStockDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("batch_number")
    private String batchNumber;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("expiration_date")
    private Date expirationDate;
}
