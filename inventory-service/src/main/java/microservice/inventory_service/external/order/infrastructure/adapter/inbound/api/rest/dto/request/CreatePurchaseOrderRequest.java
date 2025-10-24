package microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.external.order.application.command.CreatePurchaseOrderCommand;
import microservice.inventory_service.external.order.application.command.PurchaseOrderItemCommand;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.MedicineId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePurchaseOrderRequest {
    
    @NotBlank @NotNull
    private String supplierId;
    
    @NotBlank @NotNull
    private String supplierName;
    
    @NotEmpty(message = "Items list cannot be empty")
    @NotNull
    private List<PurchaseOrderItemRequest> items;
    
    @NotNull(message = "Expected delivery date is required")
    @Future(message = "Expected delivery date must be in the future")
    private LocalDateTime expectedDeliveryDate;
    
    @NotBlank(message = "Delivery location is required")
    private String deliveryLocation;
    
    @NotBlank(message = "Created by is required")
    private String createdBy;
    
    public CreatePurchaseOrderCommand toCommand() {
        List<PurchaseOrderItemCommand> itemCommands = items.stream()
            .map(PurchaseOrderItemRequest::toCommand)
            .toList();
        
        return CreatePurchaseOrderCommand.builder()
            .supplierId(supplierId)
            .supplierName(supplierName)
            .items(itemCommands)
            .expectedDeliveryDate(expectedDeliveryDate)
            .deliveryLocation(deliveryLocation)
            .createdBy(UserId.of(createdBy))
            .build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseOrderItemRequest {
        
        @NotBlank(message = "Medicine ID is required")
        private String medicineId;
        
        @NotBlank(message = "Medicine name is required")
        private String medicineName;
        
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be positive")
        private Integer quantity;
        
        @NotNull(message = "Unit cost is required")
        @DecimalMin(value = "0.01", message = "Unit cost must be positive")
        private BigDecimal unitCost;
        
        public PurchaseOrderItemCommand toCommand() {
            return PurchaseOrderItemCommand.builder()
                .medicineId(MedicineId.of(medicineId))
                .medicineName(medicineName)
                .quantity(quantity)
                .unitCost(unitCost)
                .build();
        }
    }
}