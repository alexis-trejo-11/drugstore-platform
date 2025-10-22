package microservice.inventory.application.query;

import java.time.LocalDateTime;

public record GetExpiringBatchesQuery(LocalDateTime expirationDate, Integer daysThreshold) {
}
