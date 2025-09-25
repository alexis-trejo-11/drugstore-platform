package microservice.order_service.domain.ports.input;

import microservice.order_service.application.commands.request.CancelOrderCommand;
import microservice.order_service.application.commands.request.CreateOrderCommand;
import microservice.order_service.application.commands.request.UpdateOrderStatusCommand;
import microservice.order_service.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.application.commands.response.UpdateOrderStatusCommandResponse;

public interface OrderCommandService {
    CreateOrderCommandResponse createOrder(CreateOrderCommand command);
    UpdateOrderStatusCommandResponse updateOrderStatus(UpdateOrderStatusCommand command);
    CancelOrderCommandResponse cancelOrder(CancelOrderCommand command);
    /*
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

    */
}
