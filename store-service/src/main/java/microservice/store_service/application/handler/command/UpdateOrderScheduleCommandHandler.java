package microservice.store_service.application.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.store_service.application.command.UpdateStoreScheduleCommand;
import microservice.store_service.application.handler.result.StoreOperationResult;
import microservice.store_service.domain.exception.StoreNotFoundException;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.port.StoreRepositoryPort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UpdateOrderScheduleCommandHandler {
    private final StoreRepositoryPort storeRepository;

    public StoreOperationResult handle(UpdateStoreScheduleCommand command) {
        log.info("Handling UpdateStoreScheduleCommand: {}", command);
        Store store = storeRepository.findByID(command.storeID())
                .orElseThrow(() -> new StoreNotFoundException("id", command.storeID().toString()));

        try {
            log.info("Updating schedule for store: {}", store.getId());
            store.updateSchedule(command.scheduleCommand().toDomain());

            log.info("Persisting updated schedule for store: {}", store.getId());
            storeRepository.save(store);

            log.info("Store schedule updated: {}", store.getId());
            return StoreOperationResult.updateResult(store.getId());
        } catch (Exception e) {
            log.error("Error occurred while updating store schedule: {}", e.getMessage(), e);
            throw e;
        }
    }
}
