package microservice.store_service.application;

import microservice.store_service.application.command.*;
import microservice.store_service.application.command.valueobject.*;
import microservice.store_service.application.command.status.ActivateStoreCommand;
import microservice.store_service.application.command.status.DeactivateStoreCommand;
import microservice.store_service.application.handler.result.CreateStoreResult;
import microservice.store_service.application.handler.result.StoreOperationResult;
import microservice.store_service.application.handler.result.StoreQueryResult;
import microservice.store_service.application.query.GetStoreByCodeQuery;
import microservice.store_service.application.query.GetStoreByIDQuery;
import microservice.store_service.application.query.GetStoresByStatusQuery;
import microservice.store_service.application.query.SearchStoresQuery;
import org.springframework.data.domain.Page;

public interface StoreApplicationFacade {
    CreateStoreResult createStore(CreateStoreCommand command);

    StoreOperationResult updateStoreNameAndContactInfo(UpdateStoreNameAndContactCommand command);
    StoreOperationResult updateStoreLocation(UpdateStoreLocationCommand command);
    StoreOperationResult updateScheduleInfo(UpdateStoreScheduleCommand command);

    StoreOperationResult activateStore(ActivateStoreCommand command);
    StoreOperationResult deactivateStore(DeactivateStoreCommand command);

    StoreOperationResult setUnderMaintenance(SetUnderMaintenanceCommand command);
    StoreOperationResult setTemporaryClosure(SetTemporaryClosureCommand command);

    StoreOperationResult deleteStore(DeleteStoreCommand command);
    StoreQueryResult getStoreByCode(GetStoreByCodeQuery query);
    StoreQueryResult getStoreByID(GetStoreByIDQuery query);
    Page<StoreQueryResult> searchStores(SearchStoresQuery query);
    Page<StoreQueryResult> getStoresByStatus(GetStoresByStatusQuery query);

}
