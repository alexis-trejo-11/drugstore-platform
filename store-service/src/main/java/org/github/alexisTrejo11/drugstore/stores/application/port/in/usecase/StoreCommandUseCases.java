package org.github.alexisTrejo11.drugstore.stores.application.port.in.usecase;

import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.*;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.status.ActivateStoreCommand;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.status.DeactivateStoreCommand;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.CreateStoreResult;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.StoreOperationResult;

public interface StoreCommandUseCases {
	CreateStoreResult createStore(CreateStoreCommand command);

	StoreOperationResult updateStoreNameAndContactInfo(UpdateStoreNameAndContactCommand command);

	StoreOperationResult updateStoreLocation(UpdateStoreLocationCommand command);

	StoreOperationResult updateScheduleInfo(UpdateStoreScheduleCommand command);

	StoreOperationResult activateStore(ActivateStoreCommand command);

	StoreOperationResult deactivateStore(DeactivateStoreCommand command);

	StoreOperationResult setUnderMaintenance(SetUnderMaintenanceCommand command);

	StoreOperationResult setTemporaryClosure(SetTemporaryClosureCommand command);

	StoreOperationResult deleteStore(DeleteStoreCommand command);
}
