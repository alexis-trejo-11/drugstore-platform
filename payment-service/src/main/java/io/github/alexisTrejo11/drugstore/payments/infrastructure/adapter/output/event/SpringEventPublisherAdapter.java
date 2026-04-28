package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.event;

import io.github.alexisTrejo11.drugstore.payments.core.domain.events.DomainEvent;
import io.github.alexisTrejo11.drugstore.payments.core.port.output.PaymentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter that implements the domain's PaymentEventPublisher port
 * using Spring's ApplicationEventPublisher.
 *
 * Spring ApplicationEvents are synchronous and in-process by default,
 * meaning the @EventListener in SaleDomainService runs in the same transaction.
 * This is intentional — Sale creation is part of the same atomic operation as Payment completion.
 *
 * To switch to async (Kafka/RabbitMQ in the future), only this class needs to change.
 * The domain and application layers remain completely untouched.
 */
@Component
public class SpringEventPublisherAdapter implements PaymentEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(SpringEventPublisherAdapter.class);

    private final ApplicationEventPublisher springPublisher;

		@Autowired
    public SpringEventPublisherAdapter(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        logger.debug("Publishing domain event | type={} eventId={} occurredAt={}",
                event.getEventType(), event.getEventId(), event.getOccurredOn());

        springPublisher.publishEvent(event);

        logger.debug("Domain event published | type={} eventId={}",
                event.getEventType(), event.getEventId());
    }
}