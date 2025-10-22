package microservice.purchase.application.command;

public record ReceivedItemCommand(
        int itemId,
        String  batchNumber,
        Integer receivedQuantity
) {
}
