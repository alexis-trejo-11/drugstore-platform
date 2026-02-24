package io.github.alexisTrejo11.drugstore.payments.core.port.output;

import io.github.alexisTrejo11.drugstore.payments.core.domain.events.DomainEvent;

public interface PaymentEventPublisher {
	void publish(DomainEvent event);
}
