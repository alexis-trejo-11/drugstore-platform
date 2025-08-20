package microservice.user_service.users.core.domain.ports.output;

import microservice.user_service.users.core.domain.events.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
