package microservice.inventory_service.inventory.infrastructure.adapter.outbound.persistence.repository;

import libs_kernel.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.MedicineId;
import microservice.inventory_service.inventory.domain.port.output.InventoryRepository;
import microservice.inventory_service.inventory.infrastructure.adapter.outbound.persistence.model.InventoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {
    private final EntityMapper<InventoryEntity, Inventory> mapper;
    private final JpaInventoryRepository jpaInventoryRepository;

    @Override
    public Inventory save(Inventory inventory) {
        InventoryEntity entity = mapper.fromDomain(inventory);
        InventoryEntity savedEntity = jpaInventoryRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void bulkSave(List<Inventory> inventories) {
        List<InventoryEntity> entities = mapper.fromDomains(inventories);
        jpaInventoryRepository.saveAll(entities);
    }

    @Override
    public Optional<Inventory> findById(InventoryId id) {
        return jpaInventoryRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Inventory> findByMedicineId(MedicineId medicineId) {
        return jpaInventoryRepository.findByMedicineId(medicineId.value()).map(mapper::toDomain);
    }

    @Override
    public List<Inventory> findAll() {
        List<InventoryEntity> entities = jpaInventoryRepository.findAll();
        return mapper.toDomains(entities);
    }

    @Override
    public List<Inventory> findSpec() {
        List<InventoryEntity> entities = jpaInventoryRepository.findAll();
        return mapper.toDomains(entities);
    }

    @Override
    public List<Inventory> findByStatus(InventoryStatus status) {
        return mapper.toDomains(jpaInventoryRepository.findByStatus(status));
    }

    @Override
    public List<Inventory> findLowStock() {
        List<InventoryEntity> entities = jpaInventoryRepository.findByStatus(InventoryStatus.LOW_STOCK);
        return mapper.toDomains(entities);
    }

    @Override
    public List<Inventory> findOutOfStock() {
        List<InventoryEntity> entities = jpaInventoryRepository.findByStatus(InventoryStatus.OUT_OF_STOCK);
        return mapper.toDomains(entities);
    }

    @Override
    public List<Inventory> findByWarehouseLocation(String location) {
        List<InventoryEntity> entities = jpaInventoryRepository.findByWarehouseLocation(location);
        return mapper.toDomains(entities);
    }

    @Override
    public void delete(InventoryId id) {
        jpaInventoryRepository.deleteById(id.value());
    }

    @Override
    public boolean existsByMedicineId(MedicineId medicineId) {
        return jpaInventoryRepository.existsByMedicineId(medicineId.value());
    }
}
