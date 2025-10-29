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
public class MovementResponse {
    private String id;
    private String inventoryId;
    private String batchId;
    private String movementType;
    private Integer quantity;
    private Integer previousQuantity;
    private Integer newQuantity;
    private String reason;
    private String referenceId;
    private String referenceType;
    private String performedBy;
    private String notes;
    private LocalDateTime movementDate;
}