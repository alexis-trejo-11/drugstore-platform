package io.github.alexisTrejo11.drugstore.users.user.core.ports.output;

import io.github.alexisTrejo11.drugstore.users.user.core.domain.events.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
