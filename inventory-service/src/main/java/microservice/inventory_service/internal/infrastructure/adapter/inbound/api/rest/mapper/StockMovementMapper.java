package microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import microservice.inventory_service.internal.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.response.MovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockMovementMapper implements ResponseMapper<MovementResponse, InventoryMovement> {
    @Override
    public MovementResponse toResponse(InventoryMovement inventoryMovement) {
        if (inventoryMovement == null) {
            return null;
        }
        return MovementResponse.builder()
                .id(inventoryMovement.getId() != null ? inventoryMovement.getId().value() : null)
                .inventoryId(inventoryMovement.getInventoryId() != null ? inventoryMovement.getInventoryId().value() : null)
                .batchId(inventoryMovement.getBatchId() != null ? inventoryMovement.getBatchId().value() : null)
                .movementType(inventoryMovement.getMovementType() != null ? inventoryMovement.getMovementType().name() : null)
                .quantity(inventoryMovement.getQuantity())
                .previousQuantity(inventoryMovement.getPreviousQuantity())
                .newQuantity(inventoryMovement.getNewQuantity())
                .reason(inventoryMovement.getReason())
                .referenceId(inventoryMovement.getReferenceId())
                .referenceType(inventoryMovement.getReferenceType())
                .performedBy(inventoryMovement.getPerformedBy() != null ? inventoryMovement.getPerformedBy().value() : null)
                .notes(inventoryMovement.getNotes())
                .movementDate(inventoryMovement.getMovementDate())
                .build();
    }

    @Override
    public List<MovementResponse> toResponses(List<InventoryMovement> inventoryMovements) {
        if (inventoryMovements == null) {
            return null;
        }
        return inventoryMovements.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PageResponse<MovementResponse> toResponsePage(Page<InventoryMovement> inventoryMovements) {
        if (inventoryMovements == null) return null;

        Page<MovementResponse> responsePage = inventoryMovements.map(this::toResponse);
        return PageResponse.from(responsePage);
    }
}
