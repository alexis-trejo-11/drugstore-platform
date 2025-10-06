package microservice.order_service.orders.domain.ports.input;

import microservice.order_service.orders.application.commands.request.CancelOrderCommand;
import microservice.order_service.orders.application.commands.request.CreateOrderCommand;
import microservice.order_service.orders.application.commands.request.DeleteOrderCommand;
import microservice.order_service.orders.application.commands.request.UpdateOrderStatusCommand;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.UpdateOrderStatusCommandResponse;

public interface OrderCommandService {
    CreateOrderCommandResponse createOrder(CreateOrderCommand command);
    UpdateOrderStatusCommandResponse updateOrderStatus(UpdateOrderStatusCommand command);
    CancelOrderCommandResponse cancelOrder(CancelOrderCommand command);
    void deleteOrder(DeleteOrderCommand command);
    /*
    // CRUD Operations
    Order createOrder(CustomerID userID, List<OrderItem> items,
                     DeliveryMethod deliveryMethod, DeliveryAddress deliveryAddress, String notes);
    
    Optional<Order> getOrderById(OrderID orderID);
    
    List<Order> getOrdersByCustomerId(CustomerID userID);
    
    List<Order> getAllOrders();
    
    Order updateOrder(Order order);
    
    void deleteOrder(OrderID orderID);
    
    // Status Management
    Order confirmOrder(OrderID orderID);
    
    Order startPreparingOrder(OrderID orderID);
    
    Order markOrderReadyForPickup(OrderID orderID);
    
    Order markOrderOutForDelivery(OrderID orderID);
    
    Order markOrderAsDelivered(OrderID orderID);
    
    Order markOrderAsPickedUp(OrderID orderID);
    
    Order cancelOrder(OrderID orderID);
    
    Order returnOrder(OrderID orderID);
    
    // Query Operations
    List<Order> getOrdersByStatus(OrderStatus status);
    
    List<Order> getOrdersByCustomerAndStatus(CustomerID userID, OrderStatus status);
    
    List<Order> getOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> getActiveOrdersByCustomer(CustomerID userID);
    
    long getOrderCountByStatus(OrderStatus status);
    
    // Delivery Management
    Order setEstimatedDeliveryDate(OrderID orderID, LocalDateTime estimatedDate);
    
    Order updateOrderNotes(OrderID orderID, String notes);

    */
}
