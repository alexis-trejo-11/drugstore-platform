package microservice.inventory_service.inventory.application.query;

import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
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
