package microservice.inventory_service.order.supplier_purchase.adapter.inbound.api.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.order.supplier_purchase.application.command.ReceiveOrderCommand;
import microservice.inventory_service.order.supplier_purchase.application.command.ReceivedItemCommand;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivePurchaseOrderRequest {
    @NotEmpty
    @Valid
    private List<ReceivedItemRequest> receivedItems;
    
    @NotNull
    private LocalDateTime receivedDate;
    
    @NotBlank
    private String receivedBy;
    
    public ReceiveOrderCommand toCommand(String orderId) {
        List<ReceivedItemCommand> itemCommands = receivedItems.stream()
            .map(ReceivedItemRequest::toCommand)
            .collect(Collectors.toList());
        
        return ReceiveOrderCommand.builder()
            .purchaseOrderId(PurchaseOrderId.of(orderId))
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
        
        @NotBlank(message = "PurchaseOrder item ID is required")
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