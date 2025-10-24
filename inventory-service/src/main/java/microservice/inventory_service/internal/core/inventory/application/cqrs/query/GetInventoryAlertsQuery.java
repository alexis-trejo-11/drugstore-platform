package microservice.inventory_service.internal.core.inventory.application.cqrs.query;

import microservice.inventory_service.internal.core.alert.domain.entity.valueobject.AlertStatus;
import org.springframework.data.domain.Pageable;

public record GetInventoryAlertsQuery(AlertStatus status, Pageable pagination) {
}
