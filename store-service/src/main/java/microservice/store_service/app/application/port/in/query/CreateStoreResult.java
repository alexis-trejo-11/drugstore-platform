package microservice.store_service.app.application.port.in.query;

import lombok.Builder;
import microservice.store_service.app.domain.model.valueobjects.StoreCode;
import microservice.store_service.app.domain.model.valueobjects.StoreID;

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
