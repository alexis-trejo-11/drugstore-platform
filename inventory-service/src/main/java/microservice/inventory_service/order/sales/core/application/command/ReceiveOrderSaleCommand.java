package microservice.inventory_service.order.sales.core.application.command;

import lombok.Builder;
import microservice.inventory_service.order.sales.core.domain.entity.SalesOrderItem;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.CreateSalesOrderParams;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.DeliveryMethod;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;
import microservice.inventory_service.shared.domain.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;


@Builder
public record ReceiveOrderSaleCommand(SaleOrderId id, DeliveryMethod deliveryMethod, OrderStatus status, String notes,
                                      String paymentId, String deliveryInfoId, String pickupInfoId,
                                      List<AddSaleOrderItemCommand> items) {
    public void validate() {
        if (id == null) throw new IllegalArgumentException("Payment ID cannot be null or blank");
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Order must contain at least one item");
        if (deliveryMethod == null) throw new IllegalArgumentException("Delivery method cannot be null");
        if (status == null) throw new IllegalArgumentException("Order status cannot be null");

        items.forEach(AddSaleOrderItemCommand::validate);
    }

    public CreateSalesOrderParams toCreateSalesOrderParams() {
        // TODO: Map items from command to SalesOrderItem list
        List<SalesOrderItem> orderItems = new ArrayList<>();
        return CreateSalesOrderParams.builder()
                .id(id)
                .deliveryMethod(deliveryMethod)
                .notes(notes)
                .deliveryInfoId(deliveryInfoId)
                .pickupInfoId(pickupInfoId)
                .items(orderItems)
                .build();
    }
}


