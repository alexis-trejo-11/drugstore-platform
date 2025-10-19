package microservice.store_service.domain.port.input;

import libs_kernel.page.PageResponse;
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

public interface StoreApplicationService {
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
    PageResponse<Store> searchStores(SearchStoresQuery query);
    PageResponse<Store> getStoresByStatus(GetStoresByStatusQuery query);

}
