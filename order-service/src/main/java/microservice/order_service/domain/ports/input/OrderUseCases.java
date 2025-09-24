package microservice.order_service.domain.ports.input;

import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.OrderItem;
import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.CustomerId;
import microservice.order_service.domain.models.valueobjects.DeliveryAddress;
import microservice.order_service.domain.models.valueobjects.OrderId;

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
