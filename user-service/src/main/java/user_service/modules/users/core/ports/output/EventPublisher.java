package user_service.modules.users.core.ports.output;

import user_service.modules.users.core.domain.events.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
