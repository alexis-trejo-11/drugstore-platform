package microservice.inventory.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microservice.inventory.domain.entity.enums.AlertSeverity;
import microservice.inventory.domain.entity.enums.AlertStatus;
import microservice.inventory.domain.entity.enums.AlertType;
import microservice.inventory.domain.entity.valueobject.id.AlertId;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.entity.valueobject.id.UserID;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAlert {
    private AlertId id;
    private InventoryId inventoryId;
    private AlertType alertType;
    private AlertSeverity severity;
    private String message;
    private AlertStatus status;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
    private UserID resolvedBy;
    private String resolutionNotes;
    private LocalDateTime createdAt;
}
