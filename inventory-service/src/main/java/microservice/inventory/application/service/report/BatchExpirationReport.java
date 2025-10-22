package microservice.inventory.application.service.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory.domain.entity.InventoryBatch;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchExpirationReport {
    private LocalDateTime reportDate;
    private Integer daysThreshold;
    private Integer expiringBatchesCount;
    private Integer expiredBatchesCount;
    private List<InventoryBatch> expiringBatches;
    private List<InventoryBatch> expiredBatches;
}