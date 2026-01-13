package microservice.inventory_service.inventory.core.batch.application.query;

import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.query.GetInventoryBatchesQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetInventoryBatchesQueryHandler {
    private final InventoryBatchRepository batchRepository;

    @Autowired
    public GetInventoryBatchesQueryHandler(InventoryBatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Transactional(readOnly = true)
    public Page<InventoryBatch> handle(GetInventoryBatchesQuery query) {
        return batchRepository.findByInventoryId(
                query.inventoryId(),
                query.pageable(),
                query.activeOnly()
        );
    }
}
