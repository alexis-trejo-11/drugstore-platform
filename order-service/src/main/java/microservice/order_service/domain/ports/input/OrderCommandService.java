package microservice.order_service.domain.ports.input;

import microservice.order_service.application.commands.request.CancelOrderCommand;
import microservice.order_service.application.commands.request.CreateOrderCommand;
import microservice.order_service.application.commands.request.DeleteOrderCommand;
import microservice.order_service.application.commands.request.UpdateOrderStatusCommand;
import microservice.order_service.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.application.commands.response.UpdateOrderStatusCommandResponse;

public interface OrderCommandService {
    CreateOrderCommandResponse createOrder(CreateOrderCommand command);
    UpdateOrderStatusCommandResponse updateOrderStatus(UpdateOrderStatusCommand command);
    CancelOrderCommandResponse cancelOrder(CancelOrderCommand command);
    void deleteOrder(DeleteOrderCommand command);
    /*
    // CRUD Operations
    Order createOrder(CustomerID customerId, List<OrderItem> items,
                     DeliveryMethod deliveryMethod, DeliveryAddress deliveryAddress, String notes);
    
    Optional<Order> getOrderById(OrderID orderId);
    
    List<Order> getOrdersByCustomerId(CustomerID customerId);
    
    List<Order> getAllOrders();
    
    Order updateOrder(Order order);
    
    void deleteOrder(OrderID orderId);
    
    // Status Management
    Order confirmOrder(OrderID orderId);
    
    Order startPreparingOrder(OrderID orderId);
    
    Order markOrderReadyForPickup(OrderID orderId);
    
    Order markOrderOutForDelivery(OrderID orderId);
    
    Order markOrderAsDelivered(OrderID orderId);
    
    Order markOrderAsPickedUp(OrderID orderId);
    
    Order cancelOrder(OrderID orderId);
    
    Order returnOrder(OrderID orderId);
    
    // Query Operations
    List<Order> getOrdersByStatus(OrderStatus status);
    
    List<Order> getOrdersByCustomerAndStatus(CustomerID customerId, OrderStatus status);
    
    List<Order> getOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> getActiveOrdersByCustomer(CustomerID customerId);
    
    long getOrderCountByStatus(OrderStatus status);
    
    // Delivery Management
    Order setEstimatedDeliveryDate(OrderID orderId, LocalDateTime estimatedDate);
    
    Order updateOrderNotes(OrderID orderId, String notes);

    */
}
