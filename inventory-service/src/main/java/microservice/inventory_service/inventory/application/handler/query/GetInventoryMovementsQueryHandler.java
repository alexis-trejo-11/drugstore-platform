package microservice.inventory_service.inventory.application.handler.query;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.query.GetInventoryMovementsQuery;
import microservice.inventory_service.inventory.domain.entity.InventoryMovement;
import microservice.inventory_service.inventory.domain.port.output.InventoryMovementRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetInventoryMovementsQueryHandler {
    private final InventoryMovementRepository movementRepository;

    @Transactional(readOnly = true)
    public Page<InventoryMovement> handle(GetInventoryMovementsQuery query) {
        if (query.startDate().isPresent() && query.endDate().isPresent()) {
            return movementRepository.findByInventoryIdAndDateRange(
                    query.inventoryId(),
                    query.startDate().get(),
                    query.endDate().get(),
                    query.pageable()
            );
        }

        return movementRepository.findByInventoryId(query.inventoryId(), query.pageable());
    }
}