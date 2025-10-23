package microservice.inventory_service.inventory.application.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.query.GetInventoryAlertsQuery;
import microservice.inventory_service.inventory.domain.entity.InventoryAlert;
import microservice.inventory_service.inventory.domain.port.output.InventoryAlertRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetInventoryAlertsQueryHandler {
    private final InventoryAlertRepository inventoryAlertRepository;

    public Page<InventoryAlert> handle(GetInventoryAlertsQuery query) {
        return inventoryAlertRepository.findByStatus(query.status(), query.pagination().toPageable());
    }
}
