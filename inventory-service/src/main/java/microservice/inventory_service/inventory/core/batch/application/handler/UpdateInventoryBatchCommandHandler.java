package microservice.inventory_service.inventory.core.batch.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.batch.application.command.UpdateInventoryBatchCommand;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.batch.port.output.InventoryBatchRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateInventoryBatchCommandHandler {
    private final InventoryBatchRepository batchRepository;

    public void handle(UpdateInventoryBatchCommand command) {
        InventoryBatch batch = batchRepository.findById(command.batchId())
                .orElseThrow(() -> new IllegalArgumentException("Batch not found with id: " + command.batchId()));

        var updateParams = command.toUpdateBatchBasicInfoParams();
        batch.updateBasicInfo(updateParams);

        batchRepository.save(batch);
    }
}
