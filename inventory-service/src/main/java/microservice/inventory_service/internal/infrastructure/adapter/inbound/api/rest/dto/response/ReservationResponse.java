package microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.internal.core.stock.domain.valueobject.ReservationStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationResponse {
    private String id;
    private String inventoryId;
    private String orderId;
    private Integer quantity;
    private ReservationStatus status;
    private LocalDateTime expirationTime;
    private String reason;
    private Boolean isExpired;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
