package microservice.store_service.app.application.usecase;

import microservice.store_service.app.application.port.in.command.*;
import microservice.store_service.app.application.port.in.query.CreateStoreResult;
import microservice.store_service.app.application.port.in.query.StoreOperationResult;
import microservice.store_service.app.application.port.in.command.status.ActivateStoreCommand;
import microservice.store_service.app.application.port.in.command.status.DeactivateStoreCommand;
import microservice.store_service.app.application.port.in.usecase.StoreCommandUseCases;
import microservice.store_service.app.domain.events.StoreStatusChangedEvent;
import microservice.store_service.app.domain.exception.StoreNotFoundException;
import microservice.store_service.app.domain.model.Store;
import microservice.store_service.app.domain.model.enums.StoreStatus;
import microservice.store_service.app.application.port.out.StoreEventPublisher;
import microservice.store_service.app.application.port.out.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreCommandUseCasesImpl implements StoreCommandUseCases {
    private static final Logger log = LoggerFactory.getLogger(StoreCommandUseCasesImpl.class);

    private final StoreRepository storeRepository;
    private final StoreEventPublisher eventPublisher;

    @Autowired
    public StoreCommandUseCasesImpl(StoreRepository storeRepository, StoreEventPublisher eventPublisher) {
        this.storeRepository = storeRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public CreateStoreResult createStore(CreateStoreCommand command) {
        var params = command.toCreateStoreParams();
        Store store = Store.create(params);
        Store saved = storeRepository.save(store);
        log.info("Store created with id {} and code {}", saved.getId().value(), saved.getCode().value());
        return new CreateStoreResult(saved.getId(), saved.getCode());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult updateStoreNameAndContactInfo(UpdateStoreNameAndContactCommand command) {
        Store store = storeRepository.findByID(command.id()).orElseThrow(() -> new StoreNotFoundException("id", command.id().toString()));
        store.updateInformation(command.name(), command.infoCommand().toContactInfo());
        storeRepository.save(store);
        return StoreOperationResult.updateResult(store.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult updateStoreLocation(UpdateStoreLocationCommand command) {
        Store store = storeRepository.findByID(command.id()).orElseThrow(() -> new StoreNotFoundException("id", command.id().toString()));
        store.relocate(command.geolocation().toGeolocation(), command.address().toAddress());
        storeRepository.save(store);
        return StoreOperationResult.updateResult(store.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult updateScheduleInfo(UpdateStoreScheduleCommand command) {
        Store store = storeRepository.findByID(command.id()).orElseThrow(() -> new StoreNotFoundException("id", command.id().toString()));
        store.updateSchedule(command.scheduleCommand().toDomain());
        storeRepository.save(store);
        return StoreOperationResult.updateResult(store.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult activateStore(ActivateStoreCommand command) {
        Store store = storeRepository.findByID(command.id()).orElseThrow(() -> new StoreNotFoundException("id", command.id().toString()));
        StoreStatus previous = store.getStatus();
        store.activate();
        storeRepository.save(store);
        StoreStatusChangedEvent ev = StoreStatusChangedEvent.storeActivate(store.getId(), previous);
        eventPublisher.publishStoreStatusChanged(ev);
        return StoreOperationResult.activateResult(command.id());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult deactivateStore(DeactivateStoreCommand command) {
        Store store = storeRepository.findByID(command.id()).orElseThrow(() -> new StoreNotFoundException("id", command.id().toString()));
        store.deactivate();
        storeRepository.save(store);
        return StoreOperationResult.activateResult(command.id());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult setUnderMaintenance(SetUnderMaintenanceCommand command) {
        Store store = storeRepository.findByID(command.id()).orElseThrow(() -> new StoreNotFoundException("id", command.id().toString()));
        store.putUnderMaintenance();
        storeRepository.save(store);
        return StoreOperationResult.updateResult(store.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult setTemporaryClosure(SetTemporaryClosureCommand command) {
        Store store = storeRepository.findByID(command.id()).orElseThrow(() -> new StoreNotFoundException("id", command.id().toString()));
        store.temporarilyClose();
        storeRepository.save(store);
        return StoreOperationResult.updateResult(store.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult deleteStore(DeleteStoreCommand command) {
        if (!storeRepository.existsByID(command.id())) {
            throw new StoreNotFoundException("id", command.id().toString());
        }
        storeRepository.deleteByID(command.id());
        return StoreOperationResult.deleteResult(command.id());
    }
}
