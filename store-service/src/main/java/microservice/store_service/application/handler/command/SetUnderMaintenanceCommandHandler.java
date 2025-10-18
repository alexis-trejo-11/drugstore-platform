package microservice.store_service.application.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.store_service.application.command.SetUnderMaintenanceCommand;
import microservice.store_service.application.handler.result.StoreOperationResult;
import microservice.store_service.domain.exception.StoreNotFoundException;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.port.StoreRepositoryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SetUnderMaintenanceCommandHandler {
    private final StoreRepositoryPort storeRepository;

    public StoreOperationResult handle(SetUnderMaintenanceCommand command) {
        log.info("Handling SetUnderMaintenanceCommand: {}", command);
        Store store = storeRepository.findByID(command.storeID())
            .orElseThrow(() -> new StoreNotFoundException("id", command.storeID().toString()));

        try {
            log.info("Setting store under maintenance: {}", store.getId());
            store.putUnderMaintenance();

            log.info("Persisting store under maintenance status: {}", store.getId());
            storeRepository.save(store);
            log.info("Store set under maintenance: {}", store.getId());
            return StoreOperationResult.updateResult(store.getId());
        } catch (Exception e) {
            log.error("Error occurred while setting store under maintenance: {}", e.getMessage(), e);
            throw e;
        }
    }
}
