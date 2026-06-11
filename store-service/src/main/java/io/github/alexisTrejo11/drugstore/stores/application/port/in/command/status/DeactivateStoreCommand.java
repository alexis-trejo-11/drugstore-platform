package io.github.alexisTrejo11.drugstore.stores.application.port.in.command.status;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;

@Builder
public record DeactivateStoreCommand(StoreID id) {};


