package microservice.order_service.application.handlers;

import lombok.RequiredArgsConstructor;
import microservice.order_service.application.queries.OrderQuery;
import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.valueobjects.CustomerId;
import microservice.order_service.domain.models.valueobjects.OrderId;
import microservice.order_service.domain.ports.input.OrderUseCases;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderQueryHandler {
    
    private final OrderUseCases orderUseCases;

    public List<Order> handle(OrderQuery query) {
        // Single order by ID
        if (query.orderId() != null) {
            return orderUseCases.getOrderById(OrderId.of(query.orderId()))
                    .map(List::of)
                    .orElse(List.of());
        }

        // Orders by customer and status
        if (query.customerId() != null && query.status() != null) {
            return orderUseCases.getOrdersByCustomerAndStatus(
                    CustomerId.of(query.customerId()), 
                    query.status()
            );
        }

        // Active orders by customer
        if (query.customerId() != null && Boolean.TRUE.equals(query.activeOnly())) {
            return orderUseCases.getActiveOrdersByCustomer(CustomerId.of(query.customerId()));
        }

        // Orders by customer
        if (query.customerId() != null) {
            return orderUseCases.getOrdersByCustomerId(CustomerId.of(query.customerId()));
        }

        // Orders by status
        if (query.status() != null) {
            return orderUseCases.getOrdersByStatus(query.status());
        }

        // Orders by date range
        if (query.startDate() != null && query.endDate() != null) {
            return orderUseCases.getOrdersCreatedBetween(query.startDate(), query.endDate());
        }

        // All orders (default)
        return orderUseCases.getAllOrders();
    }

    public Optional<Order> handleSingle(OrderQuery query) {
        if (query.orderId() != null) {
            return orderUseCases.getOrderById(OrderId.of(query.orderId()));
        }
        
        List<Order> orders = handle(query);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }
}
