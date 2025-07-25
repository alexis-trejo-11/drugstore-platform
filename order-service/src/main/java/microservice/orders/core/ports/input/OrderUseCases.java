package microservice.orders.core.ports.input;

import microservice.orders.core.models.Order;
import microservice.orders.core.models.OrderItem;
import microservice.orders.core.models.enums.DeliveryMethod;
import microservice.orders.core.models.enums.OrderStatus;
import microservice.orders.core.models.valueobjects.CustomerId;
import microservice.orders.core.models.valueobjects.DeliveryAddress;
import microservice.orders.core.models.valueobjects.OrderId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderUseCases {
    // CRUD Operations
    Order createOrder(CustomerId customerId, List<OrderItem> items, 
                     DeliveryMethod deliveryMethod, DeliveryAddress deliveryAddress, String notes);
    
    Optional<Order> getOrderById(OrderId orderId);
    
    List<Order> getOrdersByCustomerId(CustomerId customerId);
    
    List<Order> getAllOrders();
    
    Order updateOrder(Order order);
    
    void deleteOrder(OrderId orderId);
    
    // Status Management
    Order confirmOrder(OrderId orderId);
    
    Order startPreparingOrder(OrderId orderId);
    
    Order markOrderReadyForPickup(OrderId orderId);
    
    Order markOrderOutForDelivery(OrderId orderId);
    
    Order markOrderAsDelivered(OrderId orderId);
    
    Order markOrderAsPickedUp(OrderId orderId);
    
    Order cancelOrder(OrderId orderId);
    
    Order returnOrder(OrderId orderId);
    
    // Query Operations
    List<Order> getOrdersByStatus(OrderStatus status);
    
    List<Order> getOrdersByCustomerAndStatus(CustomerId customerId, OrderStatus status);
    
    List<Order> getOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> getActiveOrdersByCustomer(CustomerId customerId);
    
    long getOrderCountByStatus(OrderStatus status);
    
    // Delivery Management
    Order setEstimatedDeliveryDate(OrderId orderId, LocalDateTime estimatedDate);
    
    Order updateOrderNotes(OrderId orderId, String notes);
}
