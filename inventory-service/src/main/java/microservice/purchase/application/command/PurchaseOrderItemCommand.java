package microservice.purchase.application.command;

import lombok.Builder;
import microservice.inventory.domain.entity.valueobject.id.MedicineId;

import java.math.BigDecimal;

public record PurchaseOrderItemCommand(
        MedicineId medicineId,
        String medicineName,
        Integer quantity,
        BigDecimal unitCost
) {
}
