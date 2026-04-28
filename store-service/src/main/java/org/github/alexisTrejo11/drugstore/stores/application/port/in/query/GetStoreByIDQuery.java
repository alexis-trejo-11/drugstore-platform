package org.github.alexisTrejo11.drugstore.stores.application.port.in.query;

import lombok.Builder;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;
import org.codehaus.commons.nullanalysis.NotNull;

@Builder
public record GetStoreByIDQuery(@NotNull StoreID id) {
    public static GetStoreByIDQuery of(@NotNull String id) {
        return new GetStoreByIDQuery(new StoreID(id));
    }
}