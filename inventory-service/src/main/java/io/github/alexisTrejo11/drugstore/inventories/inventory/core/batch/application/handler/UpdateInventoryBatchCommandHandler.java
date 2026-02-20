package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.handler;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.command.UpdateInventoryBatchCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.port.output.InventoryBatchRepository;
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
