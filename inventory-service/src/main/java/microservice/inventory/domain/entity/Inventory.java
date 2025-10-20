package microservice.inventory.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microservice.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory.domain.entity.valueobject.id.InventoryID;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private InventoryId id;
    private String medicineId;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer reorderLevel;
    private Integer reorderQuantity;
    private Integer maximumStockLevel;
    private String warehouseLocation;
    private InventoryStatus status;
    private LocalDateTime lastRestockedDate;
    private LocalDateTime lastCountDate;
    private List<InventoryBatch> batches;
    private List<InventoryMovement> movements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void decreaseAvailableQuantity(int quantity) {
        var absQuantity = Math.abs(quantity);

        if (this.availableQuantity - absQuantity < 0) {
            throw new IllegalArgumentException("Cannot decrease available quantity more than current available quantity");
        }

        this.availableQuantity -= absQuantity;
    }

    public void decreaseReservedQuantity(int quantity) {
        var absQuantity = Math.abs(quantity);

        if (this.reservedQuantity - absQuantity < 0) {
            throw new IllegalArgumentException("Cannot decrease reserved quantity more than current reserved quantity");
        }

        this.reservedQuantity -= absQuantity;
    }

    public void decreaseTotalQuantity(int quantity) {
        var absQuantity = Math.abs(quantity);

        if (this.totalQuantity - absQuantity < 0) {
            throw new IllegalArgumentException("Cannot decrease total quantity more than current total quantity");
        }

        this.totalQuantity -= absQuantity;
    }

    public void increaseReservedQuantity(int quantity) {
        var absQuantity = Math.abs(quantity);
        this.reservedQuantity += absQuantity;
    }
}
