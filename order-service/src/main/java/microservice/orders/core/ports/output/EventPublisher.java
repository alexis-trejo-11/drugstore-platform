package microservice.orders.core.ports.output;

public interface EventPublisher {
    void publishEvent(Object event);
    void publishEvents(java.util.List<Object> events);
}
