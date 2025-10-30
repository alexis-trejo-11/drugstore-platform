package microservice.inventory_service.inventory.adapter.outbound.persistence.repository;

import libs_kernel.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.inventory.core.inventory.port.InventoryRepository;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryEntity;
import microservice.inventory_service.inventory.adapter.outbound.persistence.repository.jpa.JpaInventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Optional<Inventory> findByProductId(ProductId productId) {
        return jpaInventoryRepository.findByProductId(productId.value()).map(mapper::toDomain);
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
    public Page<Inventory> findByStatus(InventoryStatus status, Pageable pageable) {
        var inventoryEntities  = jpaInventoryRepository.findByStatus(status, pageable);
        return inventoryEntities.map(mapper::toDomain);
    }

    @Override
    public List<Inventory> findAllLowStock() {
        List<InventoryEntity> entities = jpaInventoryRepository.findByStatus(InventoryStatus.LOW_STOCK);
        return mapper.toDomains(entities);
    }

    @Override
    public Page<Inventory> findByWarehouseLocation(String location, Pageable pageable) {
        Page<InventoryEntity> entities = jpaInventoryRepository.findByWarehouseLocation(location, pageable);
        return entities.map(mapper::toDomain);
    }

    @Override
    public void delete(InventoryId id) {
        jpaInventoryRepository.deleteById(id.value());
    }

    @Override
    public boolean existsByProductId(ProductId productId) {
        return jpaInventoryRepository.existsByProductId(productId.value());
    }
}
