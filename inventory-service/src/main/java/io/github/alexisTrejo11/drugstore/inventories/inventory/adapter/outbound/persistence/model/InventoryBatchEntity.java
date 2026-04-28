package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_batches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryBatchEntity {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "inventory_id", nullable = false, length = 36)
    private String inventoryId;

    @Column(name = "batch_number", nullable = false, unique = true)
    private String batchNumber;

    @Column(name = "lot_number")
    private String lotNumber;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "manufacturing_date")
    private LocalDateTime manufacturingDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "supplier_id", length = 36)
    private String supplierId;

    @Column(name = "supplier_name")
    private String supplierName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BatchStatus status;

    @Column(name = "storage_conditions")
    private String storageConditions;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public InventoryBatchEntity(String id) {
        this.id = id;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
