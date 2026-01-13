package microservice.store_service.domain.model;

import microservice.store_service.app.domain.model.CreateStoreParams;
import microservice.store_service.app.domain.model.ReconstructParams;
import microservice.store_service.app.domain.model.Store;
import microservice.store_service.app.domain.model.enums.StoreStatus;
import microservice.store_service.app.domain.exception.StoreValidationException;
import microservice.store_service.app.domain.model.schedule.StoreSchedule;
import microservice.store_service.app.domain.model.valueobjects.ContactInfo;
import microservice.store_service.app.domain.model.valueobjects.StoreCode;
import microservice.store_service.app.domain.model.valueobjects.StoreName;
import microservice.store_service.app.domain.model.valueobjects.location.Address;
import microservice.store_service.app.domain.model.valueobjects.location.Geolocation;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StoreDomainTest {

    @Test
    void createActivateDeactivateAndReconstruct() {
        CreateStoreParams params = new CreateStoreParams(
                StoreCode.of("T-001"),
                StoreName.of("Test Store"),
                ContactInfo.of("0000", "t@test.com"),
                Address.builder().country("C").state("S").city("City").zipCode("1000").neighborhood("N").street("St").number("1").build(),
                Geolocation.of(10.0, 20.0),
                StoreSchedule.createDefault()
        );

        Store store = Store.create(params);
        assertThat(store).isNotNull();
        assertThat(store.getId()).isNotNull();
        assertThat(store.getCode().value()).isEqualTo("T-001");
        assertThat(store.getName().value()).isEqualTo("Test Store");

        LocalDateTime createdAt = store.getTimeStamps().getCreatedAt();

        store.activate();
        assertThat(store.getStatus()).isEqualTo(StoreStatus.ACTIVE);
        assertThat(store.getTimeStamps().getUpdatedAt()).isAfterOrEqualTo(createdAt);

        store.deactivate();
        assertThat(store.getStatus()).isEqualTo(StoreStatus.INACTIVE);

        // reconstruct using ReconstructParams
        ReconstructParams rp = new ReconstructParams(
                store.getId(),
                store.getCode(),
                store.getName(),
                store.getStatus(),
                store.getContactInfo(),
                store.getAddress(),
                store.getGeolocation(),
                store.getServiceSchedule(),
                store.getTimeStamps()
        );

        Store reconstructed = Store.reconstruct(rp);
        assertThat(reconstructed.getId().value()).isEqualTo(store.getId().value());
        assertThat(reconstructed.getCode().value()).isEqualTo(store.getCode().value());
    }

    @Test
    void updateInformationValidation() {
        CreateStoreParams params = new CreateStoreParams(
                StoreCode.of("T-002"),
                StoreName.of("Update Store"),
                ContactInfo.of("1111", "u@test.com"),
                Address.builder().country("C").state("S").city("City").zipCode("1001").neighborhood("N").street("St").number("2").build(),
                Geolocation.of(11.0, 21.0),
                StoreSchedule.createDefault()
        );

        Store s = Store.create(params);

        // Passing null name should throw StoreValidationException
        assertThrows(StoreValidationException.class, () -> s.updateInformation(null, ContactInfo.of("1","a@b.com")));

        // Passing null contact should throw
        assertThrows(StoreValidationException.class, () -> s.updateInformation(StoreName.of("New"), null));
    }

    @Test
    void softDeleteAndRestoreOnDomain() {
        CreateStoreParams params = new CreateStoreParams(
                StoreCode.of("T-003"),
                StoreName.of("Del Store"),
                ContactInfo.of("2222", "d@test.com"),
                Address.builder().country("C").state("S").city("City").zipCode("1002").neighborhood("N").street("St").number("3").build(),
                Geolocation.of(12.0, 22.0),
                StoreSchedule.createDefault()
        );

        Store s = Store.create(params);
        assertThat(s.getTimeStamps().getDeletedAt()).isNull();

        s.softDelete();
        assertThat(s.getTimeStamps().getDeletedAt()).isNotNull();

        s.restore();
        assertThat(s.getTimeStamps().getDeletedAt()).isNull();
    }
}

