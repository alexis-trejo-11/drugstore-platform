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

        orderRepository.save(order);
    }

}