package microservice.inventory_service.external.order.application.command;

import lombok.Builder;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.MedicineId;

import java.math.BigDecimal;

@Builder
public record PurchaseOrderItemCommand(
        MedicineId medicineId,
        String medicineName,
        Integer quantity,
        BigDecimal unitCost
) {
}
