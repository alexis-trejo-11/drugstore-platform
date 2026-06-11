package io.github.alexisTrejo11.drugstore.stores.application.port.in.query;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;

public record GetStoreByCodeQuery(@NotNull StoreCode code) {
    public static GetStoreByCodeQuery of(@NotBlank String code) {
        return new GetStoreByCodeQuery(new StoreCode(code));
    }
}