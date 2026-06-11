package io.github.alexisTrejo11.drugstore.stores.application.port.in.query;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;

@Builder
public record GetStoreByIDQuery(@NotNull StoreID id) {
    public static GetStoreByIDQuery of(@NotNull String id) {
        return new GetStoreByIDQuery(new StoreID(id));
    }
}