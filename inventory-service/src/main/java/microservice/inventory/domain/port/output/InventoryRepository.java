package microservice.inventory.domain.port.output;

import microservice.inventory.domain.entity.Inventory;
import microservice.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory.domain.entity.valueobject.id.InventoryID;
import microservice.inventory.domain.exception.InventoryException;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    Inventory save(InventoryException inventory);

    Optional<Inventory> findByID(InventoryID id);

    Optional<Inventory> findByMedicineID(String medicineId);

    List<Inventory> findSpec();

    List<Inventory> findByStatus(InventoryStatus status);

    List<Inventory> findLowStock();

    List<Inventory> findOutOfStock();

    List<Inventory> findByWarehouseLocation(String location);

    void delete(InventoryID id);

    boolean existsByMedicineID(String medicineId);
}
