package microservice.inventory_service.inventory.domain.port.output;

import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.MedicineId;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    Inventory save(Inventory inventory);
    void bulkSave(List<Inventory> inventories);

    Optional<Inventory> findById(InventoryId id);

    Optional<Inventory> findByMedicineId(MedicineId medicineId);

    List<Inventory> findAll();

    List<Inventory> findSpec();

    List<Inventory> findByStatus(InventoryStatus status);

    List<Inventory> findLowStock();

    List<Inventory> findOutOfStock();

    List<Inventory> findByWarehouseLocation(String location);

    void delete(InventoryId id);

    boolean existsByMedicineId(MedicineId medicineId);
}
