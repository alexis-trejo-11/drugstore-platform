package microservice.inventory_service.external.order.application.command;

public record ReceivedItemCommand(
        int itemId,
        String  batchNumber,
        Integer receivedQuantity
) {
}
