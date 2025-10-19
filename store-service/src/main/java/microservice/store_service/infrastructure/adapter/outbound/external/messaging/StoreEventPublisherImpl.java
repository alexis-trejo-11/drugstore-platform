package microservice.store_service.infrastructure.adapter.outbound.external.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.store_service.config.RabbitMQConfig;
import microservice.store_service.domain.events.StoreStatusChangedEvent;
import microservice.store_service.domain.port.output.StoreEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreEventPublisherImpl implements StoreEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishStoreStatusChanged(StoreStatusChangedEvent event) {
        try {
            log.info("Publishing StoreStatusChangedEvent for store: {}", event.getStoreId());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.STORE_EVENTS_EXCHANGE,
                    RabbitMQConfig.STORE_STATUS_ROUTING_KEY,
                    event
            );

            log.info("StoreStatusChangedEvent published successfully for store: {}", event.getStoreId());
        } catch (Exception e) {
            log.error("Failed to publish StoreStatusChangedEvent for store: {}", event.getStoreId(), e);
            // TODO: Retry??
        }
    }
}
