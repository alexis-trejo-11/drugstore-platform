package microservice.store_service.application.handler.command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.store_service.application.command.status.UpdateStoreInfoCommand;
import microservice.store_service.application.handler.result.StoreOperationResult;
import microservice.store_service.domain.exception.StoreNotFoundException;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.port.StoreRepositoryPort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UpdateStoreCommandHandler {
    private final StoreRepositoryPort storeRepository;

    @Transactional
    public StoreOperationResult handle(UpdateStoreInfoCommand command) {
        log.info("Handling UpdateStoreInfoCommand: {}", command);

        Store existingStore = storeRepository.findByID(command.id())
                .orElseThrow(() -> new StoreNotFoundException("id", command.id().toString()));

        log.info("Found existing store: {}. Proceeding with update.", existingStore.getId());
        try {
            log.info("Updating store: {}", existingStore.getId());
            existingStore.updateInformation(command.name(), command.infoCommand().toContactInfo());

            log.info("Persisting updated store: {}", existingStore.getId());
            storeRepository.save(existingStore);

            log.info("Store updated: {}", existingStore.getId());
            return StoreOperationResult.updateResult(existingStore.getId());
        } catch (Exception e) {
            log.error("Error occurred while updating store: {}", e.getMessage(), e);
            throw e;
        }
    }
}
