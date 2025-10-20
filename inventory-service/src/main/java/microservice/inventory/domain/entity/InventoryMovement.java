package microservice.inventory.domain.entity;

import microservice.inventory.domain.entity.enums.MovementType;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryID;
import microservice.inventory.domain.entity.valueobject.id.MovementID;
import microservice.inventory.domain.entity.valueobject.id.UserID;

import java.time.LocalDateTime;

public class InventoryMovement {
    private MovementID id;
    private InventoryID inventoryID;
    private BatchId batchID;
    private MovementType movementType;
    private Integer quantity;
    private Integer previousQuantity;
    private Integer newQuantity;
    private String reason;
    private String referenceID;
    private String referenceType;
    private UserID performedBy;
    private String notes;
    private LocalDateTime movementDate;
    private LocalDateTime createdAt;
}
