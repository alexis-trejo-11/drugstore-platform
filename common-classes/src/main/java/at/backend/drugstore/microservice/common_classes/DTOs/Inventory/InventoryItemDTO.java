package at.backend.drugstore.microservice.common_classes.DTOs.Inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InventoryItemDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("batch_number")
    private String batchNumber;

    @JsonProperty("expiration_date")
    private LocalDateTime expirationDate;

    @JsonProperty("location")
    private String location;

    @JsonProperty("last_updated")
    private LocalDateTime lastUpdated;

    @JsonProperty("reorder_point")
    private Integer reorderPoint;

    @JsonProperty("optimal_stock_level")
    private Integer optimalStockLevel;

}
