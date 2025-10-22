package microservice.inventory.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.port.output.InventoryRepository;

import java.util.List;
import java.util.Optional;

public class InventoryRepositoryImpl implements InventoryRepository {
    @Override
    public Inventory save(Inventory inventory) {
        return null;
    }

    @Override
    public Optional<Inventory> findById(InventoryId id) {
        return Optional.empty();
    }

    @Override
    public Optional<Inventory> findByMedicineId(String medicineId) {
        return Optional.empty();
    }

    @Override
    public List<Inventory> findSpec() {
        return List.of();
    }

    @Override
    public List<Inventory> findByStatus(InventoryStatus status) {
        return List.of();
    }

    @Override
    public List<Inventory> findLowStock() {
        return List.of();
    }

    @Override
    public List<Inventory> findOutOfStock() {
        return List.of();
    }

    @Override
    public List<Inventory> findByWarehouseLocation(String location) {
        return List.of();
    }

    @Override
    public void delete(InventoryId id) {

    }

    @Override
    public boolean existsByMedicineId(String medicineId) {
        return false;
    }
}
