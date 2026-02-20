package org.github.alexisTrejo11.drugstore.stores.application.port.in.command;

import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;

public record SetUnderMaintenanceCommand(StoreID id) {
}
