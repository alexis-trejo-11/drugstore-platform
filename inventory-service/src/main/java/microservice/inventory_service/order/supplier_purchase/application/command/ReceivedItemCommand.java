package microservice.inventory_service.order.supplier_purchase.application.command;


import lombok.Builder;

@Builder
public record ReceivedItemCommand(
        String itemId,
        String  batchNumber,
        Integer receivedQuantity
) {
}
