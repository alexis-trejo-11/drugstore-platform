package microservice.store_service.application.dto.command.status;

import lombok.Builder;
import microservice.store_service.domain.model.valueobjects.StoreID;

@Builder
public record DeactivateStoreCommand(StoreID id) {};


