package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertStatus;
import org.springframework.data.domain.Pageable;

public record GetInventoryAlertsQuery(AlertStatus status, Pageable pagination) {
}
