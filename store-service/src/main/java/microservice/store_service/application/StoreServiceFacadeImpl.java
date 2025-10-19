package microservice.store_service.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.store_service.application.dto.command.*;
import microservice.store_service.application.dto.command.status.ActivateStoreCommand;
import microservice.store_service.application.dto.command.status.DeactivateStoreCommand;
import microservice.store_service.application.dto.CreateStoreResult;
import microservice.store_service.application.dto.StoreOperationResult;
import microservice.store_service.application.dto.query.GetStoreByCodeQuery;
import microservice.store_service.application.dto.query.GetStoreByIDQuery;
import microservice.store_service.application.dto.query.GetStoresByStatusQuery;
import microservice.store_service.application.dto.query.SearchStoresQuery;
import microservice.store_service.domain.exception.StoreNotFoundException;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.model.valueobjects.StoreID;
import microservice.store_service.domain.port.StoreRepositoryPort;
import microservice.store_service.domain.specification.StoreSearchCriteria;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceFacadeImpl implements StoreApplicationFacade {
    private final StoreRepositoryPort storeRepository;

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public CreateStoreResult createStore(CreateStoreCommand command) {
        log.info("Handling CreateStoreCommand: {}", command);

        log.info("Creating store entity from command");
        Store store = Store.create(
                command.code(),
                command.name(),
                command.infoCommand().toContactInfo(),
                command.addressCommand().toAddress(),
                command.geoCommand().toGeolocation(),
                command.scheduleCommand().toDomain()
        );

        log.info("Persisting store: {}", store);
        Store storeCreated = storeRepository.save(store);

        log.info("Store created with ID: {} and Code: {}", storeCreated.getId(), storeCreated.getCode());
        return new CreateStoreResult(storeCreated.getId(), storeCreated.getCode());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult updateStoreNameAndContactInfo(UpdateStoreNameAndContactCommand command) {
        log.info("Handling UpdateStoreNameAndContactCommand: {}", command);
        Store store = findStoreOrThrow(command.id());

        log.info("Updating store: {}", store.getId());
        store.updateInformation(command.name(), command.infoCommand().toContactInfo());

        log.info("Persisting updated store: {}", store.getId());
        storeRepository.save(store);

        log.info("Store updated: {}", store.getId());
        return StoreOperationResult.updateResult(store.getId());

    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult updateStoreLocation(UpdateStoreLocationCommand command) {
        log.info("Handling UpdateStoreLocationCommand: {}", command);
        Store existingStore = findStoreOrThrow(command.id());

        log.info("Updating geolocation for store: {}", existingStore.getId());
        existingStore.relocate(command.geolocation().toGeolocation(), command.address().toAddress());

        log.info("Persisting updated store location: {}", existingStore.getId());
        storeRepository.save(existingStore);

        log.info("Store location updated: {}", existingStore.getId());
        return StoreOperationResult.updateResult(existingStore.getId());

    }

    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult updateScheduleInfo(UpdateStoreScheduleCommand command) {
        log.info("Handling UpdateStoreScheduleCommand: {}", command);
        Store store = findStoreOrThrow(command.id());

        log.info("Updating schedule for store: {}", store.getId());
        store.updateSchedule(command.scheduleCommand().toDomain());

        log.info("Persisting updated schedule for store: {}", store.getId());
        storeRepository.save(store);

        log.info("Store schedule updated: {}", store.getId());
        return StoreOperationResult.updateResult(store.getId());

    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult activateStore(ActivateStoreCommand command) {
        log.info("Handling ActivateStoreCommand: {}", command);
        Store store = findStoreOrThrow(command.id());

        log.info("Activating store with ID: {}", command.id());
        store.activate();

        log.info("Persisting activated store with ID: {}", command.id());
        storeRepository.save(store);

        log.info("Store with ID: {} has been activated.", command.id());
        return StoreOperationResult.activateResult(command.id());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult deactivateStore(DeactivateStoreCommand command) {
        log.info("Handling DeactivateStoreCommand: {}", command);
        Store store = findStoreOrThrow(command.id());

        log.info("Deactivating store with ID: {}", command.id());
        store.deactivate();

        log.info("Persisting deactivated store with ID: {}", command.id());
        storeRepository.save(store);

        log.info("Store with ID: {} has been deactivated.", command.id());
        return StoreOperationResult.activateResult(command.id());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult setUnderMaintenance(SetUnderMaintenanceCommand command) {
        log.info("Handling SetUnderMaintenanceCommand: {}", command);
        Store store = findStoreOrThrow(command.id());

        log.info("Setting store under maintenance: {}", store.getId());
        store.putUnderMaintenance();

        log.info("Persisting store under maintenance status: {}", store.getId());
        storeRepository.save(store);

        log.info("Store set under maintenance: {}", store.getId());
        return StoreOperationResult.updateResult(store.getId());

    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult setTemporaryClosure(SetTemporaryClosureCommand command) {
        log.info("Handling SetTemporaryClosureCommand: {}", command);
        Store store = findStoreOrThrow(command.id());

        log.info("Setting store under temporary closure: {}", store.getId());
        store.temporarilyClose();

        log.info("Persisting store temporary closure status: {}", store.getId());
        storeRepository.save(store);

        log.info("Store set under temporary closure: {}", store.getId());
        return StoreOperationResult.updateResult(store.getId());

    }

    @Override
    @Transactional
    @CacheEvict(value = {"stores", "store_searches", "store_status"}, allEntries = true)
    public StoreOperationResult deleteStore(DeleteStoreCommand command) {
        log.info("Handling DeleteStoreCommand for ID: {}", command.id());
        if (!storeRepository.existsByID(command.id())) {
            throw new StoreNotFoundException("id", command.id().toString());
        }

        log.info("Deleting store with ID: {}", command.id());
        storeRepository.deleteByID(command.id());

        log.info("Store with ID: {} deleted successfully", command.id());
        return StoreOperationResult.deleteResult(command.id());
    }

    // Query Methods
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "stores", key = "#query.code.value()")
    public Store getStoreByCode(GetStoreByCodeQuery query) {
        return storeRepository.findByCode(query.code())
                .orElseThrow(() -> new StoreNotFoundException("exactCode", query.code().value()));
    }

    @Override
    @Transactional(readOnly = true)
    //@Cacheable(value = "stores", key = "#query.id.value()")
    public Store getStoreByID(GetStoreByIDQuery query) {
        return findStoreOrThrow(query.id());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "store_searches", key = "#query.toString()")
    public Page<Store> searchStores(SearchStoresQuery query) {
        var criteria = query.toCriteria();

        List<Store> store = storeRepository.search(criteria);
        long totalCount = storeRepository.count(criteria);

        Pageable pageable = Pageable.ofSize(criteria.size()).withPage(criteria.page());
        return new PageImpl<>(store, pageable, totalCount);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "store_status", key = "#query.status.name() + '_' + #query.page + '_' + #query.size")
    public Page<Store> getStoresByStatus(GetStoresByStatusQuery query) {
        StoreSearchCriteria searchCriteria = StoreSearchCriteria.findByStatus(
                query.status(),
                query.page(),
                query.size(),
                query.sortCriteria()
        );

        List<Store> storePage = storeRepository.search(searchCriteria);
        long totalCount = storeRepository.count(searchCriteria);

        return new PageImpl<>(
                storePage,
                Pageable.ofSize(query.size()).withPage(query.page()),
                totalCount
        );
    }

    private Store findStoreOrThrow(StoreID id) {
        return storeRepository.findByID(id).orElseThrow(() -> new StoreNotFoundException("id", id.toString()));
    }
}
