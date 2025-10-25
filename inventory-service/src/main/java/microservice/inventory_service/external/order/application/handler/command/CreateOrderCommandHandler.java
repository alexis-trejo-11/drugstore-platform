package microservice.inventory_service.external.order.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.command.InsertOrderCommand;
import microservice.inventory_service.external.order.domain.entity.*;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import microservice.inventory_service.external.order.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateOrderCommandHandler {
    private final OrderRepository orderRepository;

    @Transactional
    public OrderId handle(InsertOrderCommand command) {
        if (command.isUpdate()) {
             if (!orderRepository.existsById(command.orderId())) {
                 throw new IllegalArgumentException("Order with ID " + command.orderId() + " does not exist.");
             }
        }

        List<OrderItem> items = command.items().stream()
                .map(itemCommand -> OrderItem.builder()
                        .productId(itemCommand.productId())
                        .productName(itemCommand.productName())
                        .orderedQuantity(itemCommand.quantity())
                        .unitCost(itemCommand.unitCost())
                        .build())
                .toList();

        Order order = Order.create(
                command.supplierId(),
                command.supplierName(),
                items,
                command.expectedDeliveryDate(),
                command.deliveryLocation(),
                command.createdBy()
        );

        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }

}