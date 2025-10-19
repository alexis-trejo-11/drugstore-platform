package microservice.store_service.domain.port.output;

import microservice.store_service.domain.events.StoreStatusChangedEvent;

public interface StoreEventPublisher {
    void publishStoreStatusChanged(StoreStatusChangedEvent event);
}
