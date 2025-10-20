package microservice.inventory.domain.service;


import lombok.RequiredArgsConstructor;
import microservice.inventory.domain.entity.Inventory;
import microservice.inventory.domain.entity.InventoryBatch;
import microservice.inventory.domain.entity.enums.BatchStatus;
import microservice.inventory.domain.exception.InsufficientInventoryException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryAllocationService {
    public void validateAvailability(Inventory inventory, Integer requestedQuantity) {
        if (inventory.getAvailableQuantity() < requestedQuantity) {
            throw new InsufficientInventoryException(
                    String.format("Insufficient inventory for item %s. Requested: %d, Available: %d",
                            inventory.getMedicineId(),
                            requestedQuantity,
                            inventory.getAvailableQuantity()
                    )
            );
        }
    }

    public List<InventoryBatch> allocateBatches(List<InventoryBatch> batches, Integer quantity) {
        List<InventoryBatch> availableBatches = batches.stream()
                .filter(batch -> batch.getStatus().equals(BatchStatus.ACTIVE))
                .filter(batch -> batch.getExpirationDate().isAfter(LocalDateTime.now()))
                .filter(batch -> batch.getAvailableQuantity() > 0)
                .sorted(Comparator.comparing(InventoryBatch::getExpirationDate))
                .toList();

        if (availableBatches.isEmpty()) {
            throw new InsufficientInventoryException("No available batches found");
        }

        int remainingQuantity = quantity;
        List<InventoryBatch> allocatedBatches = new java.util.ArrayList<>();
        for (var batch : availableBatches) {
            if (remainingQuantity <= 0) break;

            int allocationQuantity = Math.min(batch.getAvailableQuantity(), remainingQuantity);
            batch.setAvailableQuantity(batch.getAvailableQuantity() - allocationQuantity);
            remainingQuantity -= allocationQuantity;
            allocatedBatches.add(batch);
        }

        if (remainingQuantity > 0) {
            throw new InsufficientInventoryException(
                    String.format("Could not allocate full quantity. Remaining: %d", remainingQuantity)
            );
        }

        return allocatedBatches;
    }

    public void reserveStock(Inventory inventory, Integer quantity) {
        validateAvailability(inventory, quantity);
        inventory.decreaseAvailableQuantity(quantity);
        inventory.increaseReservedQuantity(quantity);
    }

    public void confirmReservation(Inventory inventory, Integer quantity) {
        inventory.decreaseReservedQuantity(quantity);
        inventory.decreaseTotalQuantity(quantity);
    }
}
