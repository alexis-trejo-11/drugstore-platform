package microservice.inventory_service.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(length = 50)
    private String location;

    @Column(name = "reorder_point")
    private Integer reorderPoint;

    @Column(name = "optimal_stock_level")
    private Integer optimalStockLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
