package microservice.store_service.application.command.handler;

import jakarta.transaction.Transactional;
import libs_kernel.mapper.CommandMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.store_service.application.command.CreateStoreCommand;
import microservice.store_service.application.command.result.CreateStoreResult;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.port.StoreRepositoryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateStoreCommandHandler {
    private final StoreRepositoryPort storeRepository;
    private final CommandMapper<CreateStoreCommand, Store> commandMapper;

    @Transactional
    public CreateStoreResult handle(CreateStoreCommand command) {
        log.info("Handling CreateStoreCommand: {}", command);

        try {
            Store store = commandMapper.toTarget(command);

            log.info("Validating store business rules before persistence");
            store.validatePersist();
            store.assignID();

            log.info("Persisting store: {}", store);
            Store storeCreated = storeRepository.save(store);

            log.info("Store created with ID: {} and Code: {}", storeCreated.getId(), storeCreated.getCode());
            return new CreateStoreResult(storeCreated.getId(), storeCreated.getCode());
        } catch (Exception e) {
            log.error("Error occurred while creating store: {}", e.getMessage(), e);
            throw e;
        }



    }

}
