package microservice.inventory_service.inventory.application.query;

import microservice.inventory_service.inventory.domain.entity.valueobject.id.MedicineId;

public record GetInventoryByMedicineQuery(MedicineId medicineId) {
}
