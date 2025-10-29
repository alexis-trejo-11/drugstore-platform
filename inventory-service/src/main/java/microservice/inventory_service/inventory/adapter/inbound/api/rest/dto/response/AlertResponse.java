package microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertResponse {
    private String id;
    private String inventoryId;
    private String alertType;
    private String severity;
    private String message;
    private String status;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;
    private Boolean isActive;
    private LocalDateTime createdAt;
}