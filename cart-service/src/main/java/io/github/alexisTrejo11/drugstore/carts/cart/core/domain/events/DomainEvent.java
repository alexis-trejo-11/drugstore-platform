package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.events;

import java.time.LocalDateTime;

/**
 * Marker interface for all domain events.
 * All domain events should implement this interface.
 */
public interface DomainEvent {
	LocalDateTime getOccurredAt();
}
