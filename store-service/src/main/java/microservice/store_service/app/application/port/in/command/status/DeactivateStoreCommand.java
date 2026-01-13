package microservice.store_service.app.application.port.in.command.status;

import lombok.Builder;
import microservice.store_service.app.domain.model.valueobjects.StoreID;

@Builder
public record DeactivateStoreCommand(StoreID id) {};


