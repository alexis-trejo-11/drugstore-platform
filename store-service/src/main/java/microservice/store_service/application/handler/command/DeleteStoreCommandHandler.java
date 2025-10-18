package microservice.store_service.application.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.store_service.application.command.DeleteStoreCommand;
import microservice.store_service.application.handler.result.StoreOperationResult;
import microservice.store_service.domain.exception.StoreNotFoundException;
import microservice.store_service.domain.port.StoreRepositoryPort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DeleteStoreCommandHandler {
    private final StoreRepositoryPort storeRepository;

    public StoreOperationResult handle(DeleteStoreCommand command) {
        log.info("Handling DeleteStoreCommand for ID: {}", command.id());
        if (!storeRepository.existsByID(command.id())) {
            throw new StoreNotFoundException("id", command.id().toString());
        }

        log.info("Deleting store with ID: {}", command.id());

        try {
            storeRepository.deleteByID(command.id());
            log.info("Store with ID: {} deleted successfully", command.id());

            return StoreOperationResult.deleteResult(command.id());
        } catch (Exception e) {
            log.error("Error deleting store with ID: {}", command.id(), e);
            throw e;
        }
    }
}
