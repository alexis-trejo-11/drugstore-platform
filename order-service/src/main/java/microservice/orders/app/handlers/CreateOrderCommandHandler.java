package microservice.orders.app.handlers;

import lombok.RequiredArgsConstructor;
import microservice.orders.app.commands.CreateOrderCommand;
import microservice.orders.core.models.Order;
import microservice.orders.core.models.OrderItem;
import microservice.orders.core.models.valueobjects.Money;
import microservice.orders.core.models.valueobjects.ProductId;
import microservice.orders.core.ports.input.OrderUseCases;
import org.springframework.stereotype.Service;

import java.util.Currency;
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
