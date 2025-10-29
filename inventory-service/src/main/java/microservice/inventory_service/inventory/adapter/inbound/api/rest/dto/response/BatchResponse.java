package microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchResponse {
    private String id;
    private String batchNumber;
    private String lotNumber;
    private Integer quantity;
    private Integer availableQuantity;
    private LocalDateTime manufacturingDate;
    private LocalDateTime expirationDate;
    private String supplierId;
    private String supplierName;
    private BatchStatus status;
    private String storageConditions;
    private Long daysUntilExpiration;
    private Boolean isExpired;
    private Boolean isExpiringSoon;
    private LocalDateTime receivedDate;
    private LocalDateTime createdAt;
}
