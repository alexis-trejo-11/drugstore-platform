package microservice.store_service.application;

import lombok.RequiredArgsConstructor;
import microservice.store_service.application.command.*;
import microservice.store_service.application.command.status.ActivateStoreCommand;
import microservice.store_service.application.command.status.DeactivateStoreCommand;
import microservice.store_service.application.command.valueobject.*;
import microservice.store_service.application.handler.command.*;
import microservice.store_service.application.handler.query.GetStoreByCodeQueryHandler;
import microservice.store_service.application.handler.query.GetStoreByIDQueryHandler;
import microservice.store_service.application.handler.query.GetStoreByStatusQueryHandler;
import microservice.store_service.application.handler.query.SearchStoreQueryHandler;
import microservice.store_service.application.handler.result.CreateStoreResult;
import microservice.store_service.application.handler.result.StoreOperationResult;
import microservice.store_service.application.handler.result.StoreQueryResult;
import microservice.store_service.application.query.GetStoreByCodeQuery;
import microservice.store_service.application.query.GetStoreByIDQuery;
import microservice.store_service.application.query.GetStoresByStatusQuery;
import microservice.store_service.application.query.SearchStoresQuery;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreServiceFacadeImpl implements StoreApplicationFacade {
    // Command Handlers
    private final CreateStoreCommandHandler createStoreCommandHandler;
    private final UpdateStoreNameAndContactCommandHandler updateStoreInfoCommandHandler;
    private final UpdateLocationCommandHandler updateLocationCommandHandler;
    private final UpdateOrderScheduleCommandHandler updateOrderScheduleCommandHandler;
    private final SetOrderTemporaryClosureCommandHandler setOrderTemporaryClosureCommandHandler;
    private final SetUnderMaintenanceCommandHandler setUnderMaintenanceCommandHandler;

    private final DeactivateStoreCommandHandler deactivateStoreCommandHandler;
    private final ActivateStoreCommandHandler activateStoreCommandHandler;
    private final DeleteStoreCommandHandler deleteStoreCommandHandler;

    // Query Handlers
    private final GetStoreByCodeQueryHandler getStoreByCodeQueryHandler;
    private final GetStoreByIDQueryHandler getStoreByIDQueryHandler;
    private final SearchStoreQueryHandler searchStoreQueryHandler;
    private final GetStoreByStatusQueryHandler getStoreByStatusQueryHandler;


    // Command Methods
    @Override
    public CreateStoreResult createStore(CreateStoreCommand command) {
        return createStoreCommandHandler.handle(command);
    }

    @Override
    public StoreOperationResult updateStoreNameAndContactInfo(UpdateStoreNameAndContactCommand command) {
        return updateStoreInfoCommandHandler.handle(command);
    }

    @Override
    public StoreOperationResult updateStoreLocation(UpdateStoreLocationCommand command) {
        return updateLocationCommandHandler.handle(command);
    }

    @Override
    public StoreOperationResult updateScheduleInfo(UpdateStoreScheduleCommand command) {
        return updateOrderScheduleCommandHandler.handle(command);
    }

    @Override
    public StoreOperationResult updateScheduleInfo(Schedule24HoursCommand command) {
        return null;
    }

    @Override
    public StoreOperationResult activateStore(ActivateStoreCommand command) {
        return activateStoreCommandHandler.handle(command);
    }

    @Override
    public StoreOperationResult deactivateStore(DeactivateStoreCommand command) {
        return deactivateStoreCommandHandler.handle(command);
    }

    @Override
    public StoreOperationResult setUnderMaintenance(SetUnderMaintenanceCommand command) {
        return setUnderMaintenanceCommandHandler.handle(command);
    }

    @Override
    public StoreOperationResult setTemporaryClosure(SetTemporaryClosureCommand command) {
        return setOrderTemporaryClosureCommandHandler.handle(command);
    }

    @Override
    public StoreOperationResult deleteStore(DeleteStoreCommand command) {
        return deleteStoreCommandHandler.handle(command);
    }

    // Query Methods
    @Override
    public StoreQueryResult getStoreByCode(GetStoreByCodeQuery query) {
        return getStoreByCodeQueryHandler.execute(query);
    }

    @Override
    public StoreQueryResult getStoreByID(GetStoreByIDQuery query) {
        return getStoreByIDQueryHandler.execute(query);
    }

    @Override
    public Page<StoreQueryResult> searchStores(SearchStoresQuery query) {
        return searchStoreQueryHandler.execute(query);
    }

    @Override
    public Page<StoreQueryResult> getStoresByStatus(GetStoresByStatusQuery query) {
        return getStoreByStatusQueryHandler.execute(query);
    }

}
