package microservice.inventory_service.inventory.core.inventory.application.cqrs.query;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

@Builder
public record GetInventoryMovementsQuery(
        InventoryId inventoryId,
        Optional<LocalDateTime> startDate,
        Optional<LocalDateTime> endDate,
        Pageable pageable) {

    public static GetInventoryMovementsQuery of(String inventoryId, LocalDateTime startDate, LocalDateTime endDate) {
        return GetInventoryMovementsQuery.builder()
                .inventoryId(new InventoryId(inventoryId))
                .startDate(Optional.ofNullable(startDate))
                .endDate(Optional.ofNullable(endDate))
                .pageable(Pageable.unpaged())
                .build();
    }
}
