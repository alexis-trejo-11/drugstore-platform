package microservice.store_service.app.application.port.in.command.status;

import microservice.store_service.app.domain.model.valueobjects.StoreID;

public record ActivateStoreCommand(StoreID id) {};


