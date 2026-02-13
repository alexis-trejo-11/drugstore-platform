package microservice.inventory_service.inventory.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.JpaEntityMapper;
import microservice.inventory_service.inventory.core.alert.domain.entity.InventoryAlert;
import microservice.inventory_service.inventory.core.alert.domain.entity.valueobject.AlertId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryAlertEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryAlertJpaEntityMapper implements JpaEntityMapper<InventoryAlertEntity, InventoryAlert> {

    @Override
    public InventoryAlertEntity fromDomain(InventoryAlert inventoryAlert) {
        if (inventoryAlert == null) return null;

        return InventoryAlertEntity.builder()
                .id(inventoryAlert.getId().value())
                .inventoryId(inventoryAlert.getInventoryId().value())
                .alertType(inventoryAlert.getAlertType())
                .severity(inventoryAlert.getSeverity())
                .message(inventoryAlert.getMessage())
                .status(inventoryAlert.getStatus())
                .triggeredAt(inventoryAlert.getTriggeredAt())
                .resolvedAt(inventoryAlert.getResolvedAt())
                .resolvedBy(inventoryAlert.getResolvedBy().value())
                .resolutionNotes(inventoryAlert.getResolutionNotes())
                .createdAt(inventoryAlert.getCreatedAt())
                .build();
    }

    @Override
    public InventoryAlert toDomain(InventoryAlertEntity model) {
        if (model == null) return null;

        return InventoryAlert.reconstructor()
                .id(AlertId.of(model.getId()))
                .inventoryId(InventoryId.of(model.getInventoryId()))
                .alertType(model.getAlertType())
                .severity(model.getSeverity())
                .message(model.getMessage())
                .status(model.getStatus())
                .triggeredAt(model.getTriggeredAt())
                .resolvedAt(model.getResolvedAt())
                .resolvedBy(UserId.of(model.getResolvedBy()))
                .resolutionNotes(model.getResolutionNotes())
                .createdAt(model.getCreatedAt())
                .reconstruct();
    }

    @Override
    public List<InventoryAlertEntity> fromDomains(List<InventoryAlert> inventoryAlerts) {
        if (inventoryAlerts == null || inventoryAlerts.isEmpty()) return List.of();
        return inventoryAlerts.stream()
                .map(this::fromDomain)
                .toList();
    }

    @Override
    public List<InventoryAlert> toDomains(List<InventoryAlertEntity> inventoryAlertEntities) {
        if (inventoryAlertEntities == null || inventoryAlertEntities.isEmpty()) return List.of();
        return inventoryAlertEntities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<InventoryAlert> toDomainPage(Page<InventoryAlertEntity> modelPage) {
        if (modelPage == null || modelPage.isEmpty()) return Page.empty();
        return modelPage.map(this::toDomain);
    }
}
