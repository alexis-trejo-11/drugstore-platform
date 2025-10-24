package microservice.inventory_service.internal.core.batch.application.query;

import java.time.LocalDateTime;
import java.util.Optional;

public record GetExpiringBatchesQuery(
        Optional<LocalDateTime> expirationDate,
        Optional<Integer> daysThreshold
) {}
