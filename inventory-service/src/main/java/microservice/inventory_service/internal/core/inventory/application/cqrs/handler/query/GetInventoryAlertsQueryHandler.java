package microservice.inventory_service.internal.core.inventory.application.cqrs.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryAlertsQuery;
import microservice.inventory_service.internal.core.alert.domain.entity.InventoryAlert;
import microservice.inventory_service.internal.core.alert.domain.port.InventoryAlertOutputPort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetInventoryAlertsQueryHandler {
    private final InventoryAlertOutputPort inventoryAlertRepository;

    public Page<InventoryAlert> handle(GetInventoryAlertsQuery query) {
        return inventoryAlertRepository.findByStatus(query.status(), query.pagination());
    }
}
