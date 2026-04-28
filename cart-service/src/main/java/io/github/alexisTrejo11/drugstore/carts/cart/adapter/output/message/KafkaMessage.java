package io.github.alexisTrejo11.drugstore.carts.cart.adapter.output.message;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.events.DomainEvent;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.out.DomainEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessage implements DomainEventPublisher {
	@Override
	public void publish(DomainEvent event) {

	}
}
