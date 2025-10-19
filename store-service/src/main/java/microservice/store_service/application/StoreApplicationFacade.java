package microservice.store_service.application;

import microservice.store_service.application.dto.command.*;
import microservice.store_service.application.dto.command.status.ActivateStoreCommand;
import microservice.store_service.application.dto.command.status.DeactivateStoreCommand;
import microservice.store_service.application.dto.CreateStoreResult;
import microservice.store_service.application.dto.StoreOperationResult;
import microservice.store_service.application.dto.query.GetStoreByCodeQuery;
import microservice.store_service.application.dto.query.GetStoreByIDQuery;
import microservice.store_service.application.dto.query.GetStoresByStatusQuery;
import microservice.store_service.application.dto.query.SearchStoresQuery;
import microservice.store_service.domain.model.Store;
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
    Store getStoreByCode(GetStoreByCodeQuery query);
    Store getStoreByID(GetStoreByIDQuery query);
    Page<Store> searchStores(SearchStoresQuery query);
    Page<Store> getStoresByStatus(GetStoresByStatusQuery query);

}
