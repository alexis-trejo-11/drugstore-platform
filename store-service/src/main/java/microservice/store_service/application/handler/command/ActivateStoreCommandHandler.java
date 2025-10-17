package microservice.store_service.application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.store_service.application.command.status.ActivateStoreCommand;
import microservice.store_service.application.handler.result.StoreOperationResult;
import microservice.store_service.domain.exception.StoreNotFoundException;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.port.StoreRepositoryPort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ActivateStoreCommandHandler {
    private final StoreRepositoryPort storeRepository;

    public StoreOperationResult handle(ActivateStoreCommand command) {
        Store store = storeRepository.findByID(command.id())
                .orElseThrow(() -> new StoreNotFoundException("id", command.id().toString()));

        log.info("Activating store with ID: {}", command.id());
        store.activate();
        storeRepository.save(store);
        log.info("Store with ID: {} has been activated.", command.id());

        return StoreOperationResult.activateResult(command.id());
    }
}
