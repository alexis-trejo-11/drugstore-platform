package microservice.inventory_service.Utils;

import jakarta.persistence.*;
import microservice.inventory_service.Model.InventoryItem;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_alerts")
public class StockAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Column(name = "alert_date")
    private LocalDateTime alertDate;

    @Column(name = "is_resolved")
    private boolean isResolved;

    public enum AlertType {
        LOW_STOCK, EXPIRED, NEAR_EXPIRY
    }
}

