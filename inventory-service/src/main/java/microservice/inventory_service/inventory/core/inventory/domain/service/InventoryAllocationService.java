package microservice.inventory_service.inventory.core.inventory.domain.service;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.batch.port.output.InventoryBatchRepository;
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
    private final InventoryBatchRepository inventoryBatchRepository;

    // TODO: Decrease available quantity in the batch after assignment
    public BatchId assingCompatibleBatch(Inventory inventory, Integer quantity) {
        boolean activeOnly = true;
        List<InventoryBatch> availableBatches = inventoryBatchRepository.findByInventoryId(inventory.getId(), activeOnly);

        return availableBatches.stream()
                .filter(batch -> batch.getAvailableQuantity() >= quantity)
                .min(Comparator.comparing(batch ->
                        batch.getExpirationDate() != null ?
                                batch.getExpirationDate() :
                                batch.getReceivedDate()
                ))
                .map(InventoryBatch::getId)
                .orElseThrow(() -> new InsufficientInventoryException(
                        String.format("No batch found with sufficient quantity %d for inventory %s", quantity, inventory.getId())
                ));
    }

    public void returnBatchQuantity(BatchId batchId, Integer quantity) {
        InventoryBatch batch = inventoryBatchRepository.findById(batchId).orElseThrow(
                () -> new IllegalArgumentException("Batch not found: " + batchId)
        );

        batch.returnQuantity(quantity);
        inventoryBatchRepository.save(batch);
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