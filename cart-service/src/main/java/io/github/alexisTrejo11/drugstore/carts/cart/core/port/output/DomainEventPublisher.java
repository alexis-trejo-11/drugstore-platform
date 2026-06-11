package io.github.alexisTrejo11.drugstore.carts.cart.core.port.output;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.events.DomainEvent;

public interface DomainEventPublisher {
  void publish(DomainEvent event);
}
