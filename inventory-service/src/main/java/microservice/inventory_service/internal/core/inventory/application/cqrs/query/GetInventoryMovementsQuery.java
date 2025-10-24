package microservice.inventory_service.internal.core.inventory.application.cqrs.query;

import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public record GetInventoryMovementsQuery(
        InventoryId inventoryId,
        Optional<LocalDateTime> startDate,
        Optional<LocalDateTime> endDate,
        Pageable pageable) {

    public boolean searchByDateRange() {
        return startDate.isPresent() && endDate.isPresent();
    }
}
