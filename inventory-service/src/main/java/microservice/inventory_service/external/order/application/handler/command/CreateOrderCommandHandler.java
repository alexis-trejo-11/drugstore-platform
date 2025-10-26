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
                .map(itemCommand -> OrderItem.create(
                        itemCommand.id(),
                        itemCommand.productId(),
                        itemCommand.productName(),
                        itemCommand.quantity(),
                        itemCommand.unitCost()
                ))
                .toList();

        Order order = Order.create(
                command.orderId(),
                command.supplierId(),
                command.supplierName(),
                items,
                command.expectedDeliveryDate(),
                command.deliveryLocation(),
                command.createdBy(),
                command.currency()
        );

        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }

}