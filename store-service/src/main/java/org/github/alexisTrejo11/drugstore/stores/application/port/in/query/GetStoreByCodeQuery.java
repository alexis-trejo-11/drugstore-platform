package org.github.alexisTrejo11.drugstore.stores.application.port.in.query;

import jakarta.validation.constraints.NotBlank;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;
import org.codehaus.commons.nullanalysis.NotNull;

public record GetStoreByCodeQuery(@NotNull StoreCode code) {
    public static GetStoreByCodeQuery of(@NotBlank String code) {
        return new GetStoreByCodeQuery(new StoreCode(code));
    }
}