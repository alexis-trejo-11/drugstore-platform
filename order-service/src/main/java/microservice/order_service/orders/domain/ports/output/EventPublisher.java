package microservice.order_service.orders.domain.ports.output;

public interface EventPublisher {
    void publishEvent(Object event);
    void publishEvents(java.util.List<Object> events);
}
