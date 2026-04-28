package io.github.alexisTrejo11.drugstore.products.core.domain.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OutboxEvent {
    private UUID id;
    private String aggregateId;
    private String eventType;
    private String payload;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private Integer retryCount;
    private String status; // PENDING, PROCESSED, FAILED
}
