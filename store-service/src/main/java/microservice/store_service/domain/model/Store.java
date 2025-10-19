package microservice.store_service.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import microservice.store_service.domain.exception.StoreBusinessRuleException;
import microservice.store_service.domain.model.enums.StoreStatus;
import microservice.store_service.domain.model.valueobjects.*;
import microservice.store_service.domain.model.valueobjects.location.Address;
import microservice.store_service.domain.model.valueobjects.ContactInfo;
import microservice.store_service.domain.model.valueobjects.location.Geolocation;
import microservice.store_service.domain.model.schedule.StoreSchedule;

import java.util.Objects;


@Getter
@NoArgsConstructor
public class Store {
    private StoreID id;
    private StoreCode code;
    private StoreName name;
    private StoreStatus status;
    private ContactInfo contactInfo;
    private Address address;
    private Geolocation geolocation;
    private StoreSchedule serviceSchedule;
    private StoreTimeStamps timeStamps;

    private Store(StoreID id, StoreCode code, StoreName name, StoreStatus status,
                  ContactInfo contactInfo, Address address,
                  Geolocation geolocation, StoreSchedule serviceSchedule) {
        this.id = Objects.requireNonNull(id, "Store ID cannot be null");
        this.code = Objects.requireNonNull(code, "Store exactCode cannot be null");
        this.name = Objects.requireNonNull(name, "Store value cannot be null");
        this.status = Objects.requireNonNull(status, "Store status cannot be null");
        this.contactInfo = Objects.requireNonNull(contactInfo, "Contact info cannot be null");
        this.address = Objects.requireNonNull(address, "Address cannot be null");
        this.geolocation = Objects.requireNonNull(geolocation, "Geolocation cannot be null");
        this.serviceSchedule = Objects.requireNonNull(serviceSchedule, "Schedule cannot be null");
        this.timeStamps = StoreTimeStamps.now();
    }

    public static Store create(
            StoreCode code,
            StoreName name,
            ContactInfo contactInfo,
            Address address,
            Geolocation geolocation,
            StoreSchedule serviceSchedule
    ) {
        StoreID id = StoreID.generate();
        return new Store(
                id, code, name, StoreStatus.INACTIVE,
                contactInfo, address, geolocation, serviceSchedule
        );
    }

    public static Store reconstruct(
            StoreID id,
            StoreCode code,
            StoreName name,
            StoreStatus status,
            ContactInfo contactInfo,
            Address address,
            Geolocation geolocation,
            StoreSchedule serviceSchedule,
            StoreTimeStamps timeStamps) {
        Store store = new Store(id, code, name, status, contactInfo, address, geolocation, serviceSchedule);
        store.setTimeStamps(timeStamps);
        return store;
    }

    private void setTimeStamps(StoreTimeStamps timeStamps) {
        this.timeStamps = timeStamps;
    }

    public void activate() {
        if (status == StoreStatus.ACTIVE) {
            throw new StoreBusinessRuleException("Store is already active");
        }
        this.status = StoreStatus.ACTIVE;
        this.timeStamps.markAsUpdated();
    }

    public void deactivate() {
        if (status == StoreStatus.INACTIVE) {
            throw new StoreBusinessRuleException("Store is already inactive");
        }
        this.status = StoreStatus.INACTIVE;
        this.timeStamps.markAsUpdated();
    }

    public void temporarilyClose() {
        this.status = StoreStatus.TEMPORARILY_CLOSED;
        this.timeStamps.markAsUpdated();
    }

    public void putUnderMaintenance() {
        this.status = StoreStatus.UNDER_MAINTENANCE;
        this.timeStamps.markAsUpdated();
    }

    public void updateInformation(StoreName name, ContactInfo contactInfo) {
        this.name = Objects.requireNonNull(name);
        this.contactInfo = Objects.requireNonNull(contactInfo);
        this.timeStamps.markAsUpdated();
    }

    public void relocate(Geolocation newGeolocation, Address newAddress) {
        this.geolocation = Objects.requireNonNull(newGeolocation);
        this.address = Objects.requireNonNull(newAddress);
        this.timeStamps.markAsUpdated();
    }

    public void updateSchedule(StoreSchedule newSchedule) {
        this.serviceSchedule = Objects.requireNonNull(newSchedule);
        this.timeStamps.markAsUpdated();
    }

    public void convertTo24Hours() {
        this.serviceSchedule = StoreSchedule.create24Hours();
        this.timeStamps.markAsUpdated();
    }

    public boolean isOpen() {
        return status == StoreStatus.ACTIVE && serviceSchedule.isOpenNow();
    }

    public boolean isOperational() {
        return status == StoreStatus.ACTIVE || status == StoreStatus.TEMPORARILY_CLOSED;
    }
}
