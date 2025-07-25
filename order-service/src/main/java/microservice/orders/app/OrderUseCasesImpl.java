package microservice.orders.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.orders.core.models.Order;
import microservice.orders.core.models.OrderItem;
import microservice.orders.core.models.enums.DeliveryMethod;
import microservice.orders.core.models.enums.OrderStatus;
import microservice.orders.core.models.exceptions.OrderNotFoundException;
import microservice.orders.core.models.valueobjects.CustomerId;
import microservice.orders.core.models.valueobjects.DeliveryAddress;
import microservice.orders.core.models.valueobjects.OrderId;
import microservice.orders.core.ports.input.OrderUseCases;
import microservice.orders.core.ports.output.EventPublisher;
import microservice.orders.core.ports.output.NotificationService;
import microservice.orders.core.ports.output.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderUseCasesImpl implements OrderUseCases {
    
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    private final NotificationService notificationService;

    @Override
    public Order createOrder(CustomerId customerId, List<OrderItem> items, 
                           DeliveryMethod deliveryMethod, DeliveryAddress deliveryAddress, String notes) {
        log.info("Creating order for customer: {}", customerId);
        
        Order order = Order.create(customerId, items, deliveryMethod, deliveryAddress, notes);
        Order savedOrder = orderRepository.save(order);
        
        // Publish domain events
        eventPublisher.publishEvents(savedOrder.getDomainEvents());
        savedOrder.clearDomainEvents();
        
        // Send notification
        notificationService.notifyOrderCreated(savedOrder);
        
        log.info("Order created successfully: {}", savedOrder.getId());
        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(OrderId orderId) {
        log.debug("Retrieving order by ID: {}", orderId);
        return orderRepository.findById(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerId(CustomerId customerId) {
        log.debug("Retrieving orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        log.debug("Retrieving all orders");
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrder(Order order) {
        log.info("Updating order: {}", order.getId());
        
        if (!orderRepository.existsById(order.getId())) {
            throw new OrderNotFoundException(order.getId());
        }
        
        Order updatedOrder = orderRepository.save(order);
        
        // Publish domain events
        eventPublisher.publishEvents(updatedOrder.getDomainEvents());
        updatedOrder.clearDomainEvents();
        
        return updatedOrder;
    }

    @Override
    public void deleteOrder(OrderId orderId) {
        log.info("Deleting order: {}", orderId);
        
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException(orderId);
        }
        
        orderRepository.delete(orderId);
        log.info("Order deleted successfully: {}", orderId);
    }

    @Override
    public Order confirmOrder(OrderId orderId) {
        log.info("Confirming order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.confirm();
        Order updatedOrder = orderRepository.save(order);
        
        // Publish events and notify
        publishEventsAndNotify(updatedOrder);
        
        log.info("Order confirmed: {}", orderId);
        return updatedOrder;
    }

    @Override
    public Order startPreparingOrder(OrderId orderId) {
        log.info("Starting preparation for order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.startPreparing();
        Order updatedOrder = orderRepository.save(order);
        
        publishEventsAndNotify(updatedOrder);
        
        log.info("Order preparation started: {}", orderId);
        return updatedOrder;
    }

    @Override
    public Order markOrderReadyForPickup(OrderId orderId) {
        log.info("Marking order ready for pickup: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.markReadyForPickup();
        Order updatedOrder = orderRepository.save(order);
        
        publishEventsAndNotify(updatedOrder);
        notificationService.notifyOrderReadyForPickup(updatedOrder);
        
        log.info("Order marked ready for pickup: {}", orderId);
        return updatedOrder;
    }

    @Override
    public Order markOrderOutForDelivery(OrderId orderId) {
        log.info("Marking order out for delivery: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.markOutForDelivery();
        Order updatedOrder = orderRepository.save(order);
        
        publishEventsAndNotify(updatedOrder);
        notificationService.notifyOrderOutForDelivery(updatedOrder);
        
        log.info("Order marked out for delivery: {}", orderId);
        return updatedOrder;
    }

    @Override
    public Order markOrderAsDelivered(OrderId orderId) {
        log.info("Marking order as delivered: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.markAsDelivered();
        Order updatedOrder = orderRepository.save(order);
        
        publishEventsAndNotify(updatedOrder);
        notificationService.notifyOrderDelivered(updatedOrder);
        
        log.info("Order marked as delivered: {}", orderId);
        return updatedOrder;
    }

    @Override
    public Order markOrderAsPickedUp(OrderId orderId) {
        log.info("Marking order as picked up: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.markAsPickedUp();
        Order updatedOrder = orderRepository.save(order);
        
        publishEventsAndNotify(updatedOrder);
        
        log.info("Order marked as picked up: {}", orderId);
        return updatedOrder;
    }

    @Override
    public Order cancelOrder(OrderId orderId) {
        log.info("Cancelling order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.cancel();
        Order updatedOrder = orderRepository.save(order);
        
        publishEventsAndNotify(updatedOrder);
        notificationService.notifyOrderCancelled(updatedOrder);
        
        log.info("Order cancelled: {}", orderId);
        return updatedOrder;
    }

    @Override
    public Order returnOrder(OrderId orderId) {
        log.info("Processing return for order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.returnOrder();
        Order updatedOrder = orderRepository.save(order);
        
        publishEventsAndNotify(updatedOrder);
        
        log.info("Order returned: {}", orderId);
        return updatedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        log.debug("Retrieving orders by status: {}", status);
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerAndStatus(CustomerId customerId, OrderStatus status) {
        log.debug("Retrieving orders for customer {} with status: {}", customerId, status);
        return orderRepository.findByCustomerIdAndStatus(customerId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Retrieving orders created between {} and {}", startDate, endDate);
        return orderRepository.findOrdersCreatedBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getActiveOrdersByCustomer(CustomerId customerId) {
        log.debug("Retrieving active orders for customer: {}", customerId);
        return orderRepository.findActiveOrdersByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getOrderCountByStatus(OrderStatus status) {
        log.debug("Counting orders by status: {}", status);
        return orderRepository.countByStatus(status);
    }

    @Override
    public Order setEstimatedDeliveryDate(OrderId orderId, LocalDateTime estimatedDate) {
        log.info("Setting estimated delivery date for order {}: {}", orderId, estimatedDate);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.setEstimatedDeliveryDate(estimatedDate);
        Order updatedOrder = orderRepository.save(order);
        
        log.info("Estimated delivery date set for order: {}", orderId);
        return updatedOrder;
    }

    @Override
    public Order updateOrderNotes(OrderId orderId, String notes) {
        log.info("Updating notes for order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.updateNotes(notes);
        Order updatedOrder = orderRepository.save(order);
        
        log.info("Notes updated for order: {}", orderId);
        return updatedOrder;
    }

    private void publishEventsAndNotify(Order order) {
        eventPublisher.publishEvents(order.getDomainEvents());
        order.clearDomainEvents();
        notificationService.notifyOrderStatusChanged(order);
    }
}
