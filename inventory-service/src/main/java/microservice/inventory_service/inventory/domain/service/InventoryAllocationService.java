package microservice.inventory_service.inventory.domain.service;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.domain.entity.InventoryMovement;
import microservice.inventory_service.inventory.domain.entity.enums.MovementType;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;
import microservice.inventory_service.inventory.domain.exception.InsufficientInventoryException;
import microservice.inventory_service.inventory.domain.factory.InventoryMovementFactory;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderId;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryAllocationService {

    public List<InventoryBatch> allocateBatchesForOrder(List<InventoryBatch> availableBatches, Integer quantity) {
        List<InventoryBatch> sortedBatches = availableBatches.stream()
                .filter(InventoryBatch::isActive)
                .sorted(Comparator.comparing(InventoryBatch::getExpirationDate))
                .toList();

        int remainingQuantity = quantity;
        for (InventoryBatch batch : sortedBatches) {
            if (remainingQuantity <= 0) break;

            int allocationQuantity = Math.min(batch.getAvailableQuantity(), remainingQuantity);
            batch.allocateQuantity(allocationQuantity);
            remainingQuantity -= allocationQuantity;
        }

        if (remainingQuantity > 0) {
            throw new InsufficientInventoryException(
                    String.format("Could not allocate full quantity. Remaining: %d", remainingQuantity)
            );
        }

        return sortedBatches.stream()
                .filter(batch -> batch.getQuantity() > batch.getAvailableQuantity())
                .toList();
    }

    public InventoryMovement createReservationMovement(Inventory inventory, Integer quantity, PurchaseOrderId orderId, UserId performedBy) {
        return InventoryMovementFactory.create(
                inventory.getId(),
                null,
                MovementType.RESERVATION,
                quantity,
                inventory.getAvailableQuantity() + quantity,
                inventory.getAvailableQuantity(),
                "Stock reserved for order",
                orderId.value(),
                "ORDER",
                performedBy,
                null
        );
    }

    public InventoryMovement createReleaseMovement(Inventory inventory, Integer quantity, PurchaseOrderId orderId, UserId performedBy) {
        return InventoryMovementFactory.create(
                inventory.getId(),
                null,
                MovementType.RELEASE,
                quantity,
                inventory.getAvailableQuantity() - quantity,
                inventory.getAvailableQuantity(),
                "Reservation released",
                orderId.value(),
                "ORDER",
                performedBy,
                null
        );
    }

    public InventoryMovement createSaleMovement(Inventory inventory, Integer quantity, PurchaseOrderId orderId, UserId performedBy) {
        return InventoryMovementFactory.create(
                inventory.getId(),
                null,
                MovementType.SALE,
                quantity,
                inventory.getTotalQuantity() + quantity,
                inventory.getTotalQuantity(),
                "Stock sold",
                orderId.value(),
                "ORDER",
                performedBy,
                null
        );
    }
}