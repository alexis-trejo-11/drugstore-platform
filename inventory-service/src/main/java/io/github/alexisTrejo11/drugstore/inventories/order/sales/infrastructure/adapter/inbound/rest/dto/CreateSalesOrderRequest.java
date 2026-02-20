package io.github.alexisTrejo11.drugstore.inventories.order.sales.infrastructure.adapter.inbound.rest.dto;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject.DeliveryMethod;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject.SaleOrderId;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderStatus;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.command.AddSaleOrderItemCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.command.ReceiveOrderSaleCommand;

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
        String pickupInfoId,
        String customerId

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
                .customerId(customerId)
                .pickupInfoId(pickupInfoId)
                .build();
    }

    private List<AddSaleOrderItemCommand> getOrderItemCommands() {
        List<AddSaleOrderItemCommand> itemCommands = new ArrayList<>();
        if (items != null) {
            for (var item : items) {
                var itemCommand = new AddSaleOrderItemCommand(item.id(), ProductId.of(item.productId()), item.productName(), item.quantity());
                itemCommands.add(itemCommand);
            }
        }
        return itemCommands;
    }
}

