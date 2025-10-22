package microservice.inventory.infrastructure.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory.domain.entity.Inventory;
import microservice.inventory.domain.entity.InventoryBatch;
import microservice.inventory.domain.entity.InventoryMovement;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.infrastructure.adapter.outbound.persistence.model.InventoryBatchEntity;
import microservice.inventory.infrastructure.adapter.outbound.persistence.model.InventoryEntity;
import microservice.inventory.infrastructure.adapter.outbound.persistence.model.InventoryMovementEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryEntityMapper implements EntityMapper<InventoryEntity, Inventory> {
    private final EntityMapper<InventoryBatchEntity, InventoryBatch> batchMapper;
    private final EntityMapper<InventoryMovementEntity, InventoryMovement> movementMapper;

    @Override
    public InventoryEntity fromDomains(Inventory inventory) {
        return null;
    }

    @Override
    public Inventory toDomain(InventoryEntity model) {
        return null;
    }

    @Override
    public List<InventoryEntity> fromDomains(List<Inventory> inventories) {
        return List.of();
    }

    @Override
    public List<Inventory> toDomain(List<InventoryEntity> inventoryEntities) {
        return List.of();
    }

    @Override
    public Page<Inventory> toDomainPage(Page<InventoryEntity> modelPage) {
        return null;
    }
}
