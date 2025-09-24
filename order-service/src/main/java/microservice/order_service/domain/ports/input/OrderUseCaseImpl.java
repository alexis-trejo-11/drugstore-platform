package microservice.order_service.domain.ports.input;

import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.OrderItem;
import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.CustomerId;
import microservice.order_service.domain.models.valueobjects.DeliveryAddress;
import microservice.order_service.domain.models.valueobjects.OrderId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderUseCaseImpl implements OrderUseCases{
    @Override
    public Order createOrder(CustomerId customerId, List<OrderItem> items, DeliveryMethod deliveryMethod, DeliveryAddress deliveryAddress, String notes) {
        return null;
    }

    @Override
    public Optional<Order> getOrderById(OrderId orderId) {
        return Optional.empty();
    }

    @Override
    public List<Order> getOrdersByCustomerId(CustomerId customerId) {
        return List.of();
    }

    @Override
    public List<Order> getAllOrders() {
        return List.of();
    }

    @Override
    public Order updateOrder(Order order) {
        return null;
    }

    @Override
    public void deleteOrder(OrderId orderId) {

    }

    @Override
    public Order confirmOrder(OrderId orderId) {
        return null;
    }

    @Override
    public Order startPreparingOrder(OrderId orderId) {
        return null;
    }

    @Override
    public Order markOrderReadyForPickup(OrderId orderId) {
        return null;
    }

    @Override
    public Order markOrderOutForDelivery(OrderId orderId) {
        return null;
    }

    @Override
    public Order markOrderAsDelivered(OrderId orderId) {
        return null;
    }

    @Override
    public Order markOrderAsPickedUp(OrderId orderId) {
        return null;
    }

    @Override
    public Order cancelOrder(OrderId orderId) {
        return null;
    }

    @Override
    public Order returnOrder(OrderId orderId) {
        return null;
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return List.of();
    }

    @Override
    public List<Order> getOrdersByCustomerAndStatus(CustomerId customerId, OrderStatus status) {
        return List.of();
    }

    @Override
    public List<Order> getOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return List.of();
    }

    @Override
    public List<Order> getActiveOrdersByCustomer(CustomerId customerId) {
        return List.of();
    }

    @Override
    public long getOrderCountByStatus(OrderStatus status) {
        return 0;
    }

    @Override
    public Order setEstimatedDeliveryDate(OrderId orderId, LocalDateTime estimatedDate) {
        return null;
    }

    @Override
    public Order updateOrderNotes(OrderId orderId, String notes) {
        return null;
    }
}
