package microservice.inventory_service.external.order.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.command.InsertOrderCommand;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.entity.OrderItem;
import microservice.inventory_service.external.order.domain.exception.OrderNotFoundException;
import microservice.inventory_service.external.order.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateOrderCommandHandler {
    private final OrderRepository orderRepository;

    @Transactional
    public void handle(InsertOrderCommand command) {
        if (!orderRepository.existsById(command.orderId())) {
            throw new OrderNotFoundException("Order with ID " + command.orderId() + " does not exist.");
        }

        List<OrderItem> items = command.items().stream()
                .map(itemCommand -> OrderItem.builder()
                        .productId(itemCommand.productId())
                        .productName(itemCommand.productName())
                        .orderedQuantity(itemCommand.quantity())
                        .unitCost(itemCommand.unitCost())
                        .build())
                .toList();

        Order order = Order.builder()
                .id(command.orderId())
                .supplierId(command.supplierId())
                .supplierName(command.supplierName())
                .items(items)
                .expectedDeliveryDate(command.expectedDeliveryDate())
                .deliveryLocation(command.deliveryLocation())
                .createdBy(command.createdBy())
                .build();

        orderRepository.save(order);
    }

}