package microservice.store_service.application.command;

import lombok.Builder;
import microservice.store_service.domain.model.valueobjects.StoreID;

@Builder
public record DeleteStoreCommand(StoreID id, String UserID) {};


