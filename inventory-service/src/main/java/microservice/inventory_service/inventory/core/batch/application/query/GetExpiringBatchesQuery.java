package microservice.inventory_service.inventory.core.batch.application.query;

import libs_kernel.page.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;

public record GetExpiringBatchesQuery(
        Optional<LocalDateTime> expirationDate,
        Optional<Integer> daysThreshold,
        PageRequest pageRequest
) {
    public static  GetExpiringBatchesQuery of (
            LocalDateTime expirationDate,
            Integer daysThreshold,
            PageRequest pageRequest
    ) {
        return new GetExpiringBatchesQuery(Optional.ofNullable(expirationDate), Optional.ofNullable(daysThreshold), pageRequest);
    }
}
