package org.github.alexisTrejo11.drugstore.stores.application.usecase;

import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.*;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.status.ActivateStoreCommand;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.status.DeactivateStoreCommand;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.CreateStoreResult;
import org.github.alexisTrejo11.drugstore.stores.application.port.out.StoreEventPublisher;
import org.github.alexisTrejo11.drugstore.stores.application.port.out.StoreRepository;

import org.github.alexisTrejo11.drugstore.stores.domain.exception.StoreNotFoundException;
import org.github.alexisTrejo11.drugstore.stores.domain.model.ReconstructParams;
import org.github.alexisTrejo11.drugstore.stores.domain.model.Store;
import org.github.alexisTrejo11.drugstore.stores.domain.model.enums.StoreStatus;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;

import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreName;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.AddressCommand;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.ContactInfoCommand;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.GeolocationCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StoreCommandUseCasesImplTest {

    private StoreRepository storeRepository;
    private StoreEventPublisher eventPublisher;
    private StoreCommandUseCasesImpl useCases;

    @BeforeEach
    void setUp() {
        storeRepository = Mockito.mock(StoreRepository.class);
        eventPublisher = Mockito.mock(StoreEventPublisher.class);
        useCases = new StoreCommandUseCasesImpl(storeRepository, eventPublisher);
    }

    @Test
    void createStore_shouldSaveAndReturnIds() {
        CreateStoreCommand cmd = CreateStoreCommand.builder()
                .code(StoreCode.create("ABC123"))
                .name(StoreName.of("My Store"))
                .status(StoreStatus.INACTIVE)
                .build();

        // When repository saves, return the store created from params
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateStoreResult result = useCases.createStore(cmd);

        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo(StoreCode.create("ABC123"));
        assertThat(result.storeID()).isNotNull();

        ArgumentCaptor<Store> captor = ArgumentCaptor.forClass(Store.class);
        verify(storeRepository, times(1)).save(captor.capture());
        Store saved = captor.getValue();
        assertThat(saved.getCode().value()).isEqualTo("ABC123");
        assertThat(saved.getName().value()).isEqualTo("My Store");
    }

    @Test
    void updateStoreNameAndContactInfo_shouldUpdateAndSave() {
        var id = StoreID.generate();
        Store store = Store.reconstruct(new ReconstructParams(
                id, StoreCode.create("ABC123"), StoreName.of("Old"),
                StoreStatus.INACTIVE, null, null, null, null, null
        ));

        when(storeRepository.findByID(id)).thenReturn(java.util.Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenAnswer(i -> i.getArgument(0));

        var cmd = UpdateStoreNameAndContactCommand.builder()
                .id(id)
                .name(StoreName.of("New"))
                .infoCommand(new ContactInfoCommand("123","a@b.com"))
                .build();

        var res = useCases.updateStoreNameAndContactInfo(cmd);

        assertThat(res).isNotNull();
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void updateStoreLocation_shouldRelocateAndSave() {
        var id = StoreID.generate();
        Store store = Store.reconstruct(new ReconstructParams(
                id, StoreCode.create("ABC123"), StoreName.of("S"),
                StoreStatus.INACTIVE, null, null, null, null, null
        ));

        when(storeRepository.findByID(id)).thenReturn(java.util.Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenAnswer(i -> i.getArgument(0));

        var geoCmd = new GeolocationCommand(1.0,2.0);
        var addrCmd = AddressCommand.builder()
                .country("C").state("S").city("CI").neighborhood("N").street("ST").number("1").zipCode("0000").build();

        var cmd = new UpdateStoreLocationCommand(id, geoCmd, addrCmd);
        var res = useCases.updateStoreLocation(cmd);

        assertThat(res).isNotNull();
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void updateScheduleInfo_shouldUpdateScheduleAndSave() {
        var id = StoreID.generate();
        Store store = Store.reconstruct(new ReconstructParams(
                id, StoreCode.create("ABC123"), StoreName.of("S"),
                StoreStatus.INACTIVE, null, null, null, null, null
        ));

        when(storeRepository.findByID(id)).thenReturn(Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenAnswer(i -> i.getArgument(0));

        var scheduleCmd =  OrderScheduleCommand.createStandard();
        var cmd = new UpdateStoreScheduleCommand(id, scheduleCmd);
        var res = useCases.updateScheduleInfo(cmd);

        assertThat(res).isNotNull();
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void activateStore_shouldActivateAndPublishEvent() {
        var id = StoreID.generate();
        Store store = Store.reconstruct(new ReconstructParams(
                id, StoreCode.create("ABC123"), StoreName.of("S"),
                StoreStatus.INACTIVE, null, null, null, null, null
        ));

        when(storeRepository.findByID(id)).thenReturn(java.util.Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenAnswer(i -> i.getArgument(0));

        var cmd = new ActivateStoreCommand(id);
        var res = useCases.activateStore(cmd);

        assertThat(res).isNotNull();
        verify(eventPublisher).publishStoreStatusChanged(any());
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void deactivateStore_shouldDeactivateAndSave() {
        var id = StoreID.generate();
        Store store = Store.reconstruct(new ReconstructParams(
                id, StoreCode.create("ABC123"), StoreName.of("S"),
                StoreStatus.ACTIVE, null, null, null, null, null
        ));

        when(storeRepository.findByID(id)).thenReturn(java.util.Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenAnswer(i -> i.getArgument(0));

        var cmd = new DeactivateStoreCommand(id);
        var res = useCases.deactivateStore(cmd);

        assertThat(res).isNotNull();
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void setUnderMaintenance_shouldSave() {
        var id = StoreID.generate();
        Store store = Store.reconstruct(new ReconstructParams(
                id, StoreCode.create("ABC123"), StoreName.of("S"),
                StoreStatus.ACTIVE, null, null, null, null, null
        ));

        when(storeRepository.findByID(id)).thenReturn(java.util.Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenAnswer(i -> i.getArgument(0));

        var cmd = new SetUnderMaintenanceCommand(id);
        var res = useCases.setUnderMaintenance(cmd);

        assertThat(res).isNotNull();
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void setTemporaryClosure_shouldSave() {
        var id = StoreID.generate();
        Store store = Store.reconstruct(new ReconstructParams(
                id, StoreCode.create("ABC123"), StoreName.of("S"),
                StoreStatus.ACTIVE, null, null, null, null, null
        ));

        when(storeRepository.findByID(id)).thenReturn(java.util.Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenAnswer(i -> i.getArgument(0));

        var cmd = new SetTemporaryClosureCommand(id);
        var res = useCases.setTemporaryClosure(cmd);

        assertThat(res).isNotNull();
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void deleteStore_shouldCallDeleteWhenExists() {
        var id = StoreID.generate();
        when(storeRepository.existsByID(id)).thenReturn(true);
        doNothing().when(storeRepository).deleteByID(id);

        var cmd = new DeleteStoreCommand(id);
        var res = useCases.deleteStore(cmd);

        assertThat(res).isNotNull();
        verify(storeRepository).deleteByID(id);
    }

    @Test
    void deleteStore_shouldThrowWhenNotExists() {
        var id = StoreID.generate();
        when(storeRepository.existsByID(id)).thenReturn(false);

        var cmd = new DeleteStoreCommand(id);
        org.junit.jupiter.api.Assertions.assertThrows(StoreNotFoundException.class, () -> useCases.deleteStore(cmd));
    }

}
