package microservice.inventory_service.purchase.application.command;

public record ReceivedItemCommand(
        int itemId,
        String  batchNumber,
        Integer receivedQuantity
) {
}
