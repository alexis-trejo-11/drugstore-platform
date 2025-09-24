package microservice.order_service.application.handlers;

import lombok.RequiredArgsConstructor;
import microservice.order_service.application.commands.request.CreateOrderCommand;
import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.OrderItem;
import microservice.order_service.domain.models.valueobjects.Money;
import microservice.order_service.domain.models.valueobjects.ProductId;
import microservice.order_service.domain.ports.input.OrderUseCases;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandler {
    
    private final OrderUseCases orderUseCases;

    public Order handle(CreateOrderCommand command) {
        List<OrderItem> orderItems = command.items().stream()
                .map(this::toOrderItem)
                .toList();

        return orderUseCases.createOrder(
                command.getCustomerIdVO(),
                orderItems,
                command.deliveryMethod(),
                command.deliveryAddress(),
                command.notes()
        );
    }

    private OrderItem toOrderItem(CreateOrderCommand.OrderItemCommand itemCommand) {
        return OrderItem.create(
                ProductId.of(itemCommand.productId()),
                itemCommand.productName(),
                Money.usd(itemCommand.unitPrice()),
                itemCommand.quantity()
        );
    }
}
