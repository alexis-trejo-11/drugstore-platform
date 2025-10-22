package microservice.inventory_service.purchase.domain.entity;

import microservice.inventory_service.inventory.domain.entity.valueobject.id.MedicineId;

import java.math.BigDecimal;

public class PurchaseOrderItemFactory {

    public static PurchaseOrderItem create(MedicineId medicineId, String medicineName,
                                           Integer quantity, BigDecimal unitCost) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitCost.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit cost must be positive");
        }

        BigDecimal totalCost = unitCost.multiply(BigDecimal.valueOf(quantity));

        return PurchaseOrderItem.reconstructor()
            .medicineId(medicineId)
            .medicineName(medicineName)
            .orderedQuantity(quantity)
            .receivedQuantity(0)
            .unitCost(unitCost)
            .totalCost(totalCost)
            .reconstruct();
    }
}