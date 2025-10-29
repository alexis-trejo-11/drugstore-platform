package microservice.inventory_service.order.sales.infrastructure.adapter.inbound.rest.dto;

import microservice.inventory_service.order.sales.core.domain.entity.valueobject.DeliveryMethod;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;
import microservice.inventory_service.shared.domain.order.OrderStatus;
import microservice.inventory_service.order.sales.core.application.command.AddSaleOrderItemCommand;
import microservice.inventory_service.order.sales.core.application.command.ReceiveOrderSaleCommand;

import java.util.ArrayList;
import java.util.List;

public record CreateSalesOrderRequest(
        String id,
        DeliveryMethod deliveryMethod,
        OrderStatus status,
        String notes,
        List<CreateOrderItemRequest> items,
        String paymentId,
        String deliveryInfoId,
        String pickupInfoId

) {
    public ReceiveOrderSaleCommand toCommand() {
        List<AddSaleOrderItemCommand> itemDTOS = getOrderItemCommands();
        return ReceiveOrderSaleCommand.builder()
                .id(id != null ? new SaleOrderId(id) : null)
                .deliveryMethod(deliveryMethod)
                .status(status)
                .notes(notes)
                .items(itemDTOS)
                .paymentId(paymentId)
                .deliveryInfoId(deliveryInfoId)
                .pickupInfoId(pickupInfoId)
                .build();
    }

    private List<AddSaleOrderItemCommand> getOrderItemCommands() {
        List<AddSaleOrderItemCommand> itemCommands = new ArrayList<>();
        if (items != null) {
            for (var item : items) {
                var itemCommand = new AddSaleOrderItemCommand(item.id(), item.productId(), item.productName(), item.quantity());
                itemCommands.add(itemCommand);
            }
        }
        return itemCommands;
    }
}

