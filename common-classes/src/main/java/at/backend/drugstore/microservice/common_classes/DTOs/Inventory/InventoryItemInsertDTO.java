package at.backend.drugstore.microservice.common_classes.DTOs.Inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InventoryItemInsertDTO {

    @JsonProperty("product_id")
    @NotNull(message = "product_id is obligatory")
    @Positive(message = "product_id must be positive")
    private Long productId;

    @JsonProperty("quantity")
    @NotNull(message = "quantity is obligatory")
    @Positive(message = "quantity must be positive")
    private Integer quantity;

    @JsonProperty("batch_number")
    @NotNull(message = "batch_number is obligatory")
    @NotEmpty(message = "batch_number can't be blank")
    private String batchNumber;

    @JsonProperty("expiration_date")
    @NotNull(message = "expiration_date is obligatory")
    @Future(message = "expiration_date must be on a future date")
    private LocalDateTime expirationDate;

    @JsonProperty("location")
    private String location;

    @JsonProperty("reorder_point")
    private Integer reorderPoint;

    @JsonProperty("optimal_stock_level")
    private Integer optimalStockLevel;


}
