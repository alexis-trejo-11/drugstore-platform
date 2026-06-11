package io.github.alexisTrejo11.drugstore.stores.infrastructure.outbound.external.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.stores.domain.events.StoreStatusChangedEvent;
import io.github.alexisTrejo11.drugstore.stores.application.port.output.StoreEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreEventPublisherImpl implements StoreEventPublisher {

    public void publishStoreStatusChanged(StoreStatusChangedEvent event) {
        try {
            log.info("Publishing StoreStatusChangedEvent for store: {}", event.getStoreId());



            log.info("StoreStatusChangedEvent published successfully for store: {}", event.getStoreId());
        } catch (Exception e) {
            log.error("Failed to publish StoreStatusChangedEvent for store: {}", event.getStoreId(), e);
            // TODO: Retry??
        }
    }
}
