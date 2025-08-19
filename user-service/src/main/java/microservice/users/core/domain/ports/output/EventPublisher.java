package microservice.users.core.domain.ports.output;

import microservice.users.core.domain.events.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
