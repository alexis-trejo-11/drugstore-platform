package microservice.store_service.application.command.status;

import lombok.Builder;
import microservice.store_service.domain.model.valueobjects.StoreID;

public record ActivateStoreCommand(StoreID id) {};


