package microservice.inventory_service.internal.purachse_order.application.command;


import lombok.Builder;

@Builder
public record ReceivedItemCommand(
        String itemId,
        String  batchNumber,
        Integer receivedQuantity
) {
}
