package microservice.store_service.app.infrastructure.outbound.persistence.mapper;

import libs_kernel.mapper.ModelMapper;
import microservice.store_service.app.domain.model.valueobjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import microservice.store_service.app.domain.model.Store;
import microservice.store_service.app.domain.model.valueobjects.*;
import microservice.store_service.app.domain.model.valueobjects.location.Address;
import microservice.store_service.app.domain.model.valueobjects.location.Geolocation;
import microservice.store_service.app.domain.model.schedule.StoreSchedule;
import microservice.store_service.app.infrastructure.outbound.persistence.entity.AddressEmbeddable;
import microservice.store_service.app.infrastructure.outbound.persistence.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

import microservice.store_service.app.domain.model.ReconstructParams;
import microservice.store_service.app.domain.validation.StoreValidation;

@Component
public class StoreEntityMapper implements ModelMapper<Store, StoreEntity> {
    private static final Logger logger = LoggerFactory.getLogger(StoreEntityMapper.class);

    private final ScheduleJsonMapper scheduleJsonMapper;

    public StoreEntityMapper(ScheduleJsonMapper scheduleJsonMapper) {
        this.scheduleJsonMapper = scheduleJsonMapper;
    }

    @Override
    public StoreEntity fromDomain(Store domain) {
        StoreEntity entity = new StoreEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().value());
        }
        entity.setCode(domain.getCode().value());
        entity.setName(domain.getName().value());
        entity.setStatus(domain.getStatus());

        // Contact Info
        ContactInfo contact = domain.getContactInfo();
        entity.setPhone(contact.phone());
        entity.setEmail(contact.email());

        // Address
        AddressEmbeddable addressEmbeddable = getEmbeddableAddress(domain);
        entity.setAddress(addressEmbeddable);

        // Geolocation
        Geolocation geolocation = domain.getGeolocation();
        entity.setLatitude(geolocation.latitude());
        entity.setLongitude(geolocation.longitude());

        // Schedule
        var scheduleJson = scheduleJsonMapper.toJson(domain.getServiceSchedule());
        entity.setScheduleJson(scheduleJson);

        // Timestamps
        StoreTimeStamps timestamps = domain.getTimeStamps();
        entity.setCreatedAt(timestamps.getCreatedAt());
        entity.setUpdatedAt(timestamps.getUpdatedAt());
        entity.setDeletedAt(timestamps.getDeletedAt());

        return entity;
    }

    private static AddressEmbeddable getEmbeddableAddress(Store domain) {
        AddressEmbeddable addressEmbeddable = new AddressEmbeddable();
        addressEmbeddable.setCountry(domain.getAddress().country());
        addressEmbeddable.setState(domain.getAddress().state());
        addressEmbeddable.setCity(domain.getAddress().city());
        addressEmbeddable.setNeighborhood(domain.getAddress().neighborhood());
        addressEmbeddable.setStreet(domain.getAddress().street());
        addressEmbeddable.setNumber(domain.getAddress().number());
        addressEmbeddable.setZipCode(domain.getAddress().zipCode());
        return addressEmbeddable;
    }

    @Override
    public Store toDomain(StoreEntity entity) {
        validateEntity(entity);

        return Store.reconstruct(
                new ReconstructParams(
                        new StoreID(entity.getId()),
                        StoreCode.of(entity.getCode()),
                        StoreName.of(entity.getName()),
                        entity.getStatus(),
                        reconstructContactInfo(entity),
                        reconstructAddress(entity),
                        reconstructGeolocation(entity),
                        reconstructSchedule(entity),
                        reconstructTimeStamps(entity)
                )
        );
    }

    @Override
    public List<StoreEntity> fromDomains(List<Store> stores) {
        return  stores.stream().map(this::fromDomain).toList();
    }

    @Override
    public List<Store> toDomains(List<StoreEntity> storeEntities) {
        return storeEntities.stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Store> toDomainPage(Page<StoreEntity> storeEntities) {
        return storeEntities.map(this::toDomain);
    }

    private void validateEntity(StoreEntity entity) {
        StoreValidation.requireNonNull(entity, "StoreEntity");
        StoreValidation.requireNonNull(entity.getId(), "Entity ID");
        StoreValidation.requireNonNull(entity.getStatus(), "Entity status");
        StoreValidation.requireNonBlank(entity.getCode(), "Entity exactCode");
        StoreValidation.requireNonBlank(entity.getName(), "Entity name");

        // Geolocation must exist
        StoreValidation.requireNonNull(entity.getLatitude(), "Latitude");
        StoreValidation.requireNonNull(entity.getLongitude(), "Longitude");

        // Address components
        StoreValidation.requireNonBlank(entity.getAddress().getCountry(), "Country in entity");
        StoreValidation.requireNonBlank(entity.getAddress().getState(), "State in entity");
        StoreValidation.requireNonBlank(entity.getAddress().getZipCode(), "ZipCode in entity");
        StoreValidation.requireNonBlank(entity.getAddress().getStreet(), "Street in entity");
        StoreValidation.requireNonBlank(entity.getAddress().getNumber(), "Number in entity");
        StoreValidation.requireNonBlank(entity.getAddress().getNeighborhood(), "Neighborhood in entity");

        // Timestamps
        StoreValidation.requireNonNull(entity.getCreatedAt(), "CreatedAt");
    }


    private ContactInfo reconstructContactInfo(StoreEntity entity) {
        if ((entity.getPhone() == null || entity.getPhone().isBlank()) &&
                (entity.getEmail() == null || entity.getEmail().isBlank())) {
            throw new IllegalStateException(
                    "Invalid entity status: Store must have at least one contact method");
        }

        return ContactInfo.of(entity.getPhone(), entity.getEmail());
    }

    private Address reconstructAddress(StoreEntity entity) {
        return Address.builder()
                .country(entity.getAddress().getCountry())
                .state(entity.getAddress().getState())
                .city(entity.getAddress().getCity())
                .zipCode(entity.getAddress().getZipCode())
                .neighborhood(entity.getAddress().getNeighborhood())
                .street(entity.getAddress().getStreet())
                .number(entity.getAddress().getNumber())
                .build();
    }

    private Geolocation reconstructGeolocation(StoreEntity entity) {
        return Geolocation.of(entity.getLatitude(), entity.getLongitude());
    }

    private StoreSchedule reconstructSchedule(StoreEntity entity) {
        String scheduleJson = entity.getScheduleJson();

        if (scheduleJson == null || scheduleJson.isBlank()) {
            return null;
        }

        return scheduleJsonMapper.fromJson(scheduleJson);
    }

    private StoreTimeStamps reconstructTimeStamps(StoreEntity entity) {
        return new StoreTimeStamps(
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
