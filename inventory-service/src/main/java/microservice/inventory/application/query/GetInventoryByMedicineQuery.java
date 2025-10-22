package microservice.inventory.application.query;

import microservice.inventory.domain.entity.valueobject.id.MedicineId;

public record GetInventoryByMedicineQuery(MedicineId medicineId) {
}
