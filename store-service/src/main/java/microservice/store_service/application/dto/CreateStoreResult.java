package microservice.store_service.application.dto;

import lombok.Builder;
import microservice.store_service.domain.model.valueobjects.StoreCode;
import microservice.store_service.domain.model.valueobjects.StoreID;

@Builder
public record CreateStoreResult(
        StoreID storeID,
        StoreCode code,
        String message
) {
    public CreateStoreResult(StoreID storeID, StoreCode code) {
        this(storeID, code, "Store has been created successfully.");
    }
}
