package microservice.inventory.infrastructure.adapter.outbound.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import microservice.inventory.domain.entity.enums.MovementType;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "inventory_id", nullable = false)
    private String inventoryId;

    @Column(name = "batch_id")
    private String batchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "previous_quantity")
    private Integer previousQuantity;

    @Column(name = "new_quantity")
    private Integer newQuantity;

    @Column(name = "reason")
    private String reason;

    @Column(name = "reference_id", nullable = false ,length = 36)
    private String referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "performed_by", nullable = false, length = 36)
    private String performedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
