package microservice.inventory_service.external.sales_order.controller.dto;

import microservice.inventory_service.external.sales_order.model.DeliveryMethod;
import microservice.inventory_service.external.sales_order.model.OrderStatus;
import microservice.inventory_service.external.sales_order.service.dto.SaleOrderItemDTO;
import microservice.inventory_service.external.sales_order.service.dto.SalesOrderDTO;

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
    public SalesOrderDTO toDTO() {
        List<SaleOrderItemDTO> itemDTOS = getOrderItemCommands();
        return SalesOrderDTO.builder()
                .id(id)
                .deliveryMethod(deliveryMethod)
                .status(status)
                .notes(notes)
                .items(itemDTOS)
                .paymentId(paymentId)
                .deliveryInfoId(deliveryInfoId)
                .pickupInfoId(pickupInfoId)
                .build();
    }

    private List<SaleOrderItemDTO> getOrderItemCommands() {
        List<SaleOrderItemDTO> itemCommands = new ArrayList<>();
        if (items != null) {
            for (var item : items) {
                var itemCommand = new SaleOrderItemDTO(item.id(), item.productId(), item.productName(), item.quantity());
                itemCommands.add(itemCommand);
            }
        }
        return itemCommands;
    }
}

