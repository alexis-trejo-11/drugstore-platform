package microservice.inventory.application.query;

import libs_kernel.page.Pagination;
import microservice.inventory.domain.entity.enums.AlertStatus;

public record GetInventoryAlertsQuery(AlertStatus status, Pagination pagination) {
}
