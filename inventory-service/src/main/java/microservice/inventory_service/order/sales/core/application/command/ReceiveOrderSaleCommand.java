package microservice.inventory_service.order.sales.core.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.order.sales.core.domain.entity.SalesOrderItem;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.CreateSalesOrderParams;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.DeliveryMethod;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;
import microservice.inventory_service.shared.domain.order.OrderStatus;
import microservice.inventory_service.shared.exception.MissingFieldException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public record ReceiveOrderSaleCommand(SaleOrderId id, DeliveryMethod deliveryMethod, OrderStatus status, String notes,
                                      String paymentId, String deliveryInfoId, String pickupInfoId,
                                      List<AddSaleOrderItemCommand> items, String customerId) {

    @Builder
    public ReceiveOrderSaleCommand(SaleOrderId id, DeliveryMethod deliveryMethod, OrderStatus status, String notes,
                                   String paymentId, String deliveryInfoId, String pickupInfoId,
                                   List<AddSaleOrderItemCommand> items, String customerId) {
        this.id = id;
        this.deliveryMethod = deliveryMethod;
        this.status = status;
        this.notes = notes;
        this.paymentId = paymentId;
        this.deliveryInfoId = deliveryInfoId;
        this.pickupInfoId = pickupInfoId;
        this.items = items;
        this.customerId = customerId;

        validate();
    }

    public void validate() {
        if (id == null) throw new MissingFieldException("ReceiveOrderSaleCommand", "id");
        if (items == null || items.isEmpty())
            throw new MissingFieldException("ReceiveOrderSaleCommand", "items");
        if (deliveryMethod == null) throw new MissingFieldException("ReceiveOrderSaleCommand", "deliveryMethod");
        if (status == null) throw new MissingFieldException("ReceiveOrderSaleCommand", "status");
        if (customerId == null || customerId.isBlank())
            throw new MissingFieldException("ReceiveOrderSaleCommand", "customerId");

        items.forEach(AddSaleOrderItemCommand::validate);
    }

    public CreateSalesOrderParams toCreateSalesOrderParams() {
        List<SalesOrderItem> orderItems = new ArrayList<>();
        for (AddSaleOrderItemCommand itemCommand : items) {
            var orderItem = SalesOrderItem.create(
                    itemCommand.id(),
                    itemCommand.productId(),
                    itemCommand.productName(),
                    itemCommand.quantity()
            );
            orderItems.add(orderItem);
        }

        return CreateSalesOrderParams.builder()
                .id(id)
                .deliveryMethod(deliveryMethod)
                .notes(notes)
                .deliveryInfoId(deliveryInfoId)
                .pickupInfoId(pickupInfoId)
                .items(orderItems)
                .build();
    }

    public Map<ProductId, Integer> productQuantities() {
        return items.stream()
                .collect(Collectors.toMap(
                        AddSaleOrderItemCommand::productId,
                        AddSaleOrderItemCommand::quantity
                ));
    }
}


