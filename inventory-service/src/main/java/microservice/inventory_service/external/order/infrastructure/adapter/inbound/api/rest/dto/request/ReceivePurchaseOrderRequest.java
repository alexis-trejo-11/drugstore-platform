package microservice.inventory_service.external.order.infrastructure.adapter.inbound.api.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.external.order.application.command.ReceivePurchaseOrderCommand;
import microservice.inventory_service.external.order.application.command.ReceivedItemCommand;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrderId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivePurchaseOrderRequest {
    
    @NotEmpty(message = "Received items list cannot be empty")
    @Valid
    private List<ReceivedItemRequest> receivedItems;
    
    @NotNull(message = "Received date is required")
    private LocalDateTime receivedDate;
    
    @NotBlank(message = "Received by is required")
    private String receivedBy;
    
    public ReceivePurchaseOrderCommand toCommand(String purchaseOrderId) {
        List<ReceivedItemCommand> itemCommands = receivedItems.stream()
            .map(ReceivedItemRequest::toCommand)
            .collect(Collectors.toList());
        
        return ReceivePurchaseOrderCommand.builder()
            .purchaseOrderId(PurchaseOrderId.of(purchaseOrderId))
            .receivedItems(itemCommands)
            .receivedDate(receivedDate)
            .receivedBy(UserId.of(receivedBy))
            .build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceivedItemRequest {
        
        @NotBlank(message = "Order item ID is required")
        private String orderItemId;
        
        @NotNull(message = "Received quantity is required")
        @Min(value = 1, message = "Received quantity must be positive")
        private Integer receivedQuantity;
        
        @NotBlank(message = "Batch number is required")
        private String batchNumber;
        
        public ReceivedItemCommand toCommand() {
            return ReceivedItemCommand.builder()
                .itemId(orderItemId)
                .receivedQuantity(receivedQuantity)
                .batchNumber(batchNumber)
                .build();
        }
    }
}