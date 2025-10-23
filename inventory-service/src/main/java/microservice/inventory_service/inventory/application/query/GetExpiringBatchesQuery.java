package microservice.inventory_service.inventory.application.query;

import java.time.LocalDateTime;
import java.util.Optional;

public record GetExpiringBatchesQuery(
        Optional<LocalDateTime> expirationDate,
        Optional<Integer> daysThreshold
) {}
