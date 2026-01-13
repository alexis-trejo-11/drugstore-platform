package microservice.store_service.infrastructure.outbound.persistence;

import microservice.store_service.app.domain.model.CreateStoreParams;
import microservice.store_service.app.domain.model.Store;
import microservice.store_service.app.domain.model.schedule.StoreSchedule;
import microservice.store_service.app.domain.model.valueobjects.ContactInfo;
import microservice.store_service.app.domain.model.valueobjects.StoreCode;
import microservice.store_service.app.domain.model.valueobjects.StoreName;
import microservice.store_service.app.domain.model.valueobjects.location.Address;
import microservice.store_service.app.domain.model.valueobjects.location.Geolocation;
import microservice.store_service.app.domain.model.valueobjects.StoreID;
import microservice.store_service.app.application.port.out.StoreRepository;
import microservice.store_service.app.domain.specification.StoreSearchCriteria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.data.domain.Page;
import microservice.store_service.app.domain.model.enums.StoreStatus;
import microservice.store_service.app.infrastructure.outbound.persistence.repository.JpaStoreRepository;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.fail-fast=false",
        "spring.main.allow-bean-definition-overriding=true"
})
@ActiveProfiles("test")
public class JpaStoreRepositoryIntegrationTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private JpaStoreRepository jpaStoreRepository;

    @Test
    @Transactional
    void softDeleteAndRestoreFlow() {
        // Build domain store
        CreateStoreParams params = new CreateStoreParams(
                StoreCode.of("S-001"),
                StoreName.of("My Store"),
                ContactInfo.of("123456789", "store@example.com"),
                Address.builder()
                        .country("Country")
                        .state("State")
                        .city("City")
                        .zipCode("00000")
                        .neighborhood("Neighborhood")
                        .street("Street")
                        .number("1")
                        .build(),
                Geolocation.of(10.0, 20.0),
                StoreSchedule.createDefault()
        );

        Store store = Store.create(params);

        // Save
        Store saved = storeRepository.save(store);
        assertThat(saved).isNotNull();
        StoreID id = saved.getId();

        // Exists and find
        Optional<Store> found = storeRepository.findByID(id);
        assertThat(found).isPresent();
        assertThat(found.get().getCode().value()).isEqualTo("S-001");

        // Soft delete
        storeRepository.deleteByID(id);

        // After deletion, findByID should return empty
        Optional<Store> afterDelete = storeRepository.findByID(id);
        assertThat(afterDelete).isEmpty();

        // After deletion, existence checks should be false
        assertThat(storeRepository.existsByID(id)).isFalse();
        assertThat(storeRepository.existsByCode(StoreCode.of("S-001"))).isFalse();

        // Restore
        storeRepository.restoreByID(id);

        // After restore, findByID should return present
        Optional<Store> afterRestore = storeRepository.findByID(id);
        assertThat(afterRestore).isPresent();
        assertThat(afterRestore.get().getCode().value()).isEqualTo("S-001");

        // After restore, existence checks should be true again
        assertThat(storeRepository.existsByID(id)).isTrue();
        assertThat(storeRepository.existsByCode(StoreCode.of("S-001"))).isTrue();
    }

    @Test
    @Transactional
    void repositoryCrudAndSearchFlow() {
        // Create and save multiple stores
        Store a = Store.create(
                new CreateStoreParams(
                        StoreCode.of("S-101"),
                        StoreName.of("A Store"),
                        ContactInfo.of("1111111", "a@example.com"),
                        Address.builder().country("C").state("S").city("City").zipCode("00001").neighborhood("N").street("St").number("1").build(),
                        Geolocation.of(1.0, 1.0),
                        StoreSchedule.createDefault()
                )
        );

        Store b = Store.create(
                new CreateStoreParams(
                        StoreCode.of("S-102"),
                        StoreName.of("B Store"),
                        ContactInfo.of("2222222", "b@example.com"),
                        Address.builder().country("C").state("S").city("City").zipCode("00002").neighborhood("N").street("St").number("2").build(),
                        Geolocation.of(2.0, 2.0),
                        StoreSchedule.createDefault()
                )
        );

        Store c = Store.create(
                new CreateStoreParams(
                        StoreCode.of("S-103"),
                        StoreName.of("C Store"),
                        ContactInfo.of("3333333", "c@example.com"),
                        Address.builder().country("C").state("S").city("City").zipCode("00003").neighborhood("N").street("St").number("3").build(),
                        Geolocation.of(3.0, 3.0),
                        StoreSchedule.createDefault()
                )
        );

        Store savedA = storeRepository.save(a);
        Store savedB = storeRepository.save(b);
        Store savedC = storeRepository.save(c);

        // Existence checks
        assertThat(storeRepository.existsByCode(StoreCode.of("S-101"))).isTrue();
        assertThat(storeRepository.existsByCode(StoreCode.of("S-102"))).isTrue();
        assertThat(storeRepository.existsByCode(StoreCode.of("S-103"))).isTrue();

        assertThat(storeRepository.existsByID(savedA.getId())).isTrue();
        assertThat(storeRepository.existsByID(savedB.getId())).isTrue();
        assertThat(storeRepository.existsByID(savedC.getId())).isTrue();

        // Find by code and id
        assertThat(storeRepository.findByCode(StoreCode.of("S-102"))).isPresent()
                .map(Store::getCode).map(StoreCode::value).contains("S-102");

        assertThat(storeRepository.findByID(savedB.getId())).isPresent()
                .map(Store::getId).map(StoreID::value).isPresent();

        // Search (paged) - default criteria returns page 0 size 20 sorted by name asc
        Page<Store> page = storeRepository.search(StoreSearchCriteria.findAll());
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).hasSize(3);
        // Verify ordering and names
        assertThat(page.getContent()).extracting(s -> s.getName().value())
                .containsExactly("A Store", "B Store", "C Store");

        // Also test pagination: page size 2
        Page<Store> paged = storeRepository.search(StoreSearchCriteria.findByStatus(StoreStatus.INACTIVE, 0, 2, StoreSearchCriteria.SortCriteria.NAME_ASC));
        assertThat(paged.getTotalElements()).isEqualTo(3);
        assertThat(paged.getSize()).isEqualTo(2);
        assertThat(paged.getNumberOfElements()).isEqualTo(2);

        // Count
        long count = storeRepository.count(StoreSearchCriteria.findAll());
        assertThat(count).isEqualTo(3);
    }

    @Test
    @Transactional
    void softDeleteExposureAndRestoreViaJpaRepository() {
        // Save a store
        Store store = Store.create(
                new CreateStoreParams(
                        StoreCode.of("S-501"),
                        StoreName.of("Jpa Store"),
                        ContactInfo.of("5555555", "jpa@example.com"),
                        Address.builder().country("C").state("S").city("City").zipCode("00501").neighborhood("N").street("St").number("5").build(),
                        Geolocation.of(5.0, 5.0),
                        StoreSchedule.createDefault()
                )
        );

        Store saved = storeRepository.save(store);
        StoreID id = saved.getId();

        // Ensure visible via JPA default (non-deleted)
        assertThat(jpaStoreRepository.findById(id.value())).isPresent();
        assertThat(jpaStoreRepository.findByCode(saved.getCode().value())).isPresent();

        // Soft delete using domain repository
        storeRepository.deleteByID(id);

        // After soft-delete, JPA findById (non-include) should be empty
        assertThat(jpaStoreRepository.findById(id.value())).isNotPresent();
        assertThat(jpaStoreRepository.findByCode(saved.getCode().value())).isNotPresent();

        // But include-deleted queries should return the entity
        assertThat(jpaStoreRepository.findByIdIncludeDeleted(id.value())).isPresent();
        assertThat(jpaStoreRepository.findByCodeIncludeDeleted(saved.getCode().value())).isPresent();

        // Restore using domain repository
        storeRepository.restoreByID(id);

        // After restore, non-include JPA find should return the entity again
        assertThat(jpaStoreRepository.findById(id.value())).isPresent();
        assertThat(jpaStoreRepository.findByCode(saved.getCode().value())).isPresent();
    }
}
