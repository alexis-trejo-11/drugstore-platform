package microservice.store_service.application.command.result;

import lombok.Builder;
import microservice.store_service.domain.model.valueobjects.StoreID;

@Builder
public record CreateStoreResult(
        StoreID storeID,
        String code,
        String message
) {
    public CreateStoreResult(StoreID storeID, String code) {
        this(storeID, code, "Store has been created successfully.");
    }
}
