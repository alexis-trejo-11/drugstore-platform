package microservice.inventory_service.inventory.core.inventory.domain.service;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.inventory.core.movement.domain.valueobject.MovementType;
import microservice.inventory_service.inventory.core.movement.domain.valueobject.CreateMovementParams;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.inventory.core.inventory.domain.exception.InsufficientInventoryException;
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

    // TODO: PREV AND NEW QUANTITY IS RIGHT?
    public InventoryMovement createReservationMovement(Inventory inventory, Integer quantity, PurchaseOrderId purchaseOrderId, UserId performedBy) {
        var params = CreateMovementParams.builder()
                .inventoryId(inventory.getId())
                .movementType(MovementType.RESERVATION)
                .quantity(quantity)
                .previousQuantity(inventory.getAvailableQuantity() - quantity)
                .newQuantity(inventory.getAvailableQuantity())
                .reason("Stock reserved for order")
                .referenceId(purchaseOrderId.value())
                .referenceType("ORDER")
                .performedBy(performedBy)
                .build();

        return InventoryMovement.create(params);
    }

    public InventoryMovement createReleaseMovement(Inventory inventory, Integer quantity, PurchaseOrderId purchaseOrderId, UserId performedBy) {
        var params = CreateMovementParams.builder()
                .inventoryId(inventory.getId())
                .movementType(MovementType.RELEASE)
                .quantity(quantity)
                .previousQuantity(inventory.getAvailableQuantity() + quantity)
                .newQuantity(inventory.getAvailableQuantity())
                .reason("Reservation released")
                .referenceId(purchaseOrderId.value())
                .referenceType("ORDER")
                .performedBy(performedBy)
                .build();

        return InventoryMovement.create(params);
    }

    public InventoryMovement createSaleMovement(Inventory inventory, Integer quantity, PurchaseOrderId purchaseOrderId, UserId performedBy) {
        var params = CreateMovementParams.builder()
                .inventoryId(inventory.getId())
                .movementType(MovementType.SALE)
                .quantity(quantity)
                .previousQuantity(inventory.getAvailableQuantity() + quantity)
                .newQuantity(inventory.getAvailableQuantity())
                .reason("Stock sold for order")
                .referenceId(purchaseOrderId.value())
                .referenceType("ORDER")
                .performedBy(performedBy)
                .build();
        return InventoryMovement.create(params);
    }
}