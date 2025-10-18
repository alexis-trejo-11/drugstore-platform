package microservice.store_service.application.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.store_service.application.command.UpdateStoreLocationCommand;
import microservice.store_service.application.handler.result.StoreOperationResult;
import microservice.store_service.domain.exception.StoreNotFoundException;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.port.StoreRepositoryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateLocationCommandHandler {
    private final StoreRepositoryPort storeRepository;

    public StoreOperationResult handle(UpdateStoreLocationCommand command) {
        log.info("Handling UpdateStoreLocationCommand: {}", command);

        Store existingStore = storeRepository.findByID(command.storeID())
                .orElseThrow(() -> new StoreNotFoundException("id", command.storeID().toString()));
        try {
            log.info("Updating geolocation for store: {}", existingStore.getId());
            existingStore.relocate(command.geolocation().toGeolocation(), command.address().toAddress());

            log.info("Persisting updated store location: {}", existingStore.getId());
            storeRepository.save(existingStore);

            log.info("Store location updated: {}", existingStore.getId());
            return StoreOperationResult.updateResult(existingStore.getId());
        } catch (Exception e) {
            log.error("Error occurred while updating store location: {}", e.getMessage(), e);
            throw e;
        }
    }
}

