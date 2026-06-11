package io.github.alexisTrejo11.drugstore.stores.application.port.output;

import io.github.alexisTrejo11.drugstore.stores.domain.events.StoreStatusChangedEvent;

public interface StoreEventPublisher {
  void  publishStoreStatusChanged(StoreStatusChangedEvent event);
}
