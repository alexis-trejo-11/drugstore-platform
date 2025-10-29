package microservice.inventory_service.inventory.core.inventory.application.cqrs.query;

import microservice.inventory_service.inventory.core.alert.domain.entity.valueobject.AlertStatus;
import org.springframework.data.domain.Pageable;

public record GetInventoryAlertsQuery(AlertStatus status, Pageable pagination) {
}
