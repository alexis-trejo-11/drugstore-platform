package microservice.inventory_service.purchase.application.command;

import microservice.inventory_service.inventory.domain.entity.valueobject.id.MedicineId;

import java.math.BigDecimal;

public record PurchaseOrderItemCommand(
        MedicineId medicineId,
        String medicineName,
        Integer quantity,
        BigDecimal unitCost
) {
}
