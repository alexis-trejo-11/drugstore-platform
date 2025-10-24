package microservice.inventory_service.internal.core.inventory.application.cqrs.query;

import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.MedicineId;

public record GetInventoryByMedicineQuery(MedicineId medicineId) {
    public static GetInventoryByMedicineQuery of(String medicineId) {
        return new GetInventoryByMedicineQuery(MedicineId.of(medicineId));
    }
}
