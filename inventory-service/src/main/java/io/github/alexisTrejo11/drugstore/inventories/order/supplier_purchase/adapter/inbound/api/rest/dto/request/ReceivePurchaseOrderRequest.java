package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.adapter.inbound.api.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command.ReceiveOrderCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command.ReceivedItemCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "Request DTO to record the receipt of a purchase order")
public record ReceivePurchaseOrderRequest(
    @Schema(description = "List of received items including quantity and batch info", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Valid
    List<ReceivedItemRequest> receivedItems,

    @Schema(description = "Date and time when items were received (ISO-8601)", example = "2025-11-01T10:00:00", format = "date-time")
    @NotNull
    LocalDateTime receivedDate,

    @Schema(description = "Identifier of the user who received the items", example = "user-42")
    @NotBlank
    String receivedBy
) {

    public ReceiveOrderCommand toCommand(String orderId) {
        List<ReceivedItemCommand> itemCommands = receivedItems.stream()
            .map(ReceivedItemRequest::toCommand)
            .collect(Collectors.toList());

        return ReceiveOrderCommand.builder()
            .purchaseOrderId(PurchaseOrderId.of(orderId))
            .receivedItemCmd(itemCommands)
            .receivedDate(receivedDate)
            .receivedBy(UserId.of(receivedBy))
            .build();
    }

    @Schema(description = "Single received item with quantity and batch number")
    public record ReceivedItemRequest(
        @Schema(description = "Identifier of the order item", example = "item-1")
        @NotBlank
        String itemId,

        @Schema(description = "Quantity actually received for this item", example = "5")
        @NotNull
        @Min(value = 1)
        Integer receivedQuantity,

        @Schema(description = "Batch number associated with the received items", example = "BATCH-2025-001")
        @NotBlank
        String batchNumber
    ) {
        public ReceivedItemCommand toCommand() {
            return ReceivedItemCommand.builder()
                .itemId(itemId)
                .receivedQuantity(receivedQuantity)
                .batchNumber(batchNumber)
                .build();
        }
    }
}