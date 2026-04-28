package org.github.alexisTrejo11.drugstore.stores.application.port.in.query;

import lombok.Builder;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;

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
