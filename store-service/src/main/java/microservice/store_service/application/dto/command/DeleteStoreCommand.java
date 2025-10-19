package microservice.store_service.application.dto.command;

import lombok.Builder;
import microservice.store_service.domain.model.valueobjects.StoreID;

@Builder
public record DeleteStoreCommand(StoreID id) {};


