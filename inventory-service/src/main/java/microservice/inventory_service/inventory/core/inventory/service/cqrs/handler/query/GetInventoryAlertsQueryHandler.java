package microservice.inventory_service.inventory.core.inventory.service.cqrs.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.query.GetInventoryAlertsQuery;
import microservice.inventory_service.inventory.core.alert.domain.entity.InventoryAlert;
import microservice.inventory_service.inventory.core.alert.domain.port.InventoryAlertRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetInventoryAlertsQueryHandler {
    private final InventoryAlertRepository inventoryAlertRepository;

    public Page<InventoryAlert> handle(GetInventoryAlertsQuery query) {
        return inventoryAlertRepository.findByStatus(query.status(), query.pagination());
    }
}
