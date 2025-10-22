package microservice.inventory.factory;

import microservice.inventory.domain.entity.InventoryBatch;
import microservice.inventory.domain.entity.enums.BatchStatus;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryBatchFactory {

    public static InventoryBatch create(InventoryId inventoryId, String batchNumber, String lotNumber,
                                        Integer quantity, BigDecimal costPerUnit,
                                        LocalDateTime manufacturingDate, LocalDateTime expirationDate,
                                        String supplierId, String supplierName, String storageConditions) {
        validateParameters(quantity, expirationDate);

        return InventoryBatch.reconstructor()
                .id(BatchId.generate())
                .inventoryId(inventoryId)
                .batchNumber(batchNumber)
                .lotNumber(lotNumber)
                .quantity(quantity)
                .availableQuantity(quantity)
                .costPerUnit(costPerUnit)
                .manufacturingDate(manufacturingDate)
                .expirationDate(expirationDate)
                .supplierId(supplierId)
                .supplierName(supplierName)
                .status(BatchStatus.ACTIVE)
                .storageConditions(storageConditions)
                .receivedDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .reconstruct();
    }

    private static void validateParameters(Integer quantity, LocalDateTime expirationDate) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiration date cannot be in the past");
        }
    }
}
