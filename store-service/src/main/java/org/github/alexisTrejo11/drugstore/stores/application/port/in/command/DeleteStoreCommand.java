package org.github.alexisTrejo11.drugstore.stores.application.port.in.command;

import lombok.Builder;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;

@Builder
public record DeleteStoreCommand(StoreID id) {};


