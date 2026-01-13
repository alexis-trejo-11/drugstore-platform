package microservice.store_service.app.application.port.in.usecase;

import microservice.store_service.app.application.port.in.command.*;
import microservice.store_service.app.application.port.in.command.status.ActivateStoreCommand;
import microservice.store_service.app.application.port.in.command.status.DeactivateStoreCommand;
import microservice.store_service.app.application.port.in.query.CreateStoreResult;
import microservice.store_service.app.application.port.in.query.StoreOperationResult;

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
