package microservice.inventory_service.inventory.core.batch.application.query;

import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchStatus;
import microservice.inventory_service.inventory.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.query.GetInventoryBatchesQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class GetInventoryBatchesQueryHandler {
    private final InventoryBatchRepository batchRepository;

    @Autowired
    public GetInventoryBatchesQueryHandler(InventoryBatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryBatch> handle(GetInventoryBatchesQuery query) {
        List<InventoryBatch> batches = batchRepository.findByInventoryId(query.inventoryId());

        if (query.activeOnly() != null && query.activeOnly()) {
            return batches.stream()
                    .filter(batch -> batch.getStatus() == BatchStatus.ACTIVE)
                    .toList();
        }

        return batches;
    }
}
