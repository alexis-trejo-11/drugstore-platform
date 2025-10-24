package microservice.inventory_service.external.order.application.command;


import lombok.Builder;

@Builder
public record ReceivedItemCommand(
        String itemId,
        String  batchNumber,
        Integer receivedQuantity
) {
}
