package microservice.cart_service.app.cart.adapter.output.message;

import microservice.cart_service.app.cart.core.domain.events.DomainEvent;
import microservice.cart_service.app.cart.core.port.out.DomainEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessage implements DomainEventPublisher {
	@Override
	public void publish(DomainEvent event) {

	}
}
