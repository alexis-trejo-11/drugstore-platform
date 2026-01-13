package microservice.store_service.app.domain.model;

import microservice.store_service.app.domain.exception.StoreBusinessRuleException;
import microservice.store_service.app.domain.model.enums.StoreStatus;
import microservice.store_service.app.domain.model.valueobjects.*;
import microservice.store_service.app.domain.model.valueobjects.location.Address;
import microservice.store_service.app.domain.model.valueobjects.location.Geolocation;
import microservice.store_service.app.domain.model.schedule.StoreSchedule;
import microservice.store_service.app.domain.validation.StoreValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Store {
    private static final Logger logger = LoggerFactory.getLogger(Store.class);

    private final StoreID id;
    private final StoreCode code;
    private StoreName name;
    private StoreStatus status;
    private ContactInfo contactInfo;
    private Address address;
    private Geolocation geolocation;
    private StoreSchedule serviceSchedule;
    private StoreTimeStamps timeStamps;

    private Store() {
        // default values for frameworks and reconstruction
        this.id = null;
        this.code = StoreCode.NONE;
        this.name = StoreName.NONE;
        this.status = StoreStatus.UNKNOWN;
        this.contactInfo = ContactInfo.NONE;
        this.address = Address.NONE;
        this.geolocation = Geolocation.NONE;
        this.serviceSchedule = StoreSchedule.NONE;
        this.timeStamps = StoreTimeStamps.now();
    }

    private Store(StoreID id, StoreCode code, StoreName name, StoreStatus status,
                  ContactInfo contactInfo, Address address,
                  Geolocation geolocation, StoreSchedule serviceSchedule) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.contactInfo = contactInfo;
        this.address = address;
        this.geolocation = geolocation;
        this.serviceSchedule = serviceSchedule;
        this.timeStamps = StoreTimeStamps.now();
    }

    public static Store create(CreateStoreParams params) {
        StoreValidation.requireNonNull(params, "CreateStoreParams");
        StoreValidation.requireNonNull(params.code(), "Store code");
        StoreValidation.requireNonNull(params.name(), "Store name");

        Store newStore = new Store();
        // use generated id and final code via reflection: create a new instance with final fields set
        StoreID id = StoreID.generate();
        Store store = new Store(id, params.code(), params.name(), StoreStatus.INACTIVE,
                params.contactInfo(), params.address(), params.geolocation(), params.serviceSchedule());
        logger.info("Created new store with id {} and code {}", id.value(), params.code().value());
        return store;
    }

    public static Store reconstruct(ReconstructParams params) {
        StoreValidation.requireNonNull(params, "ReconstructParams");
        StoreValidation.requireNonNull(params.id(), "Store ID");
        StoreValidation.requireNonNull(params.code(), "Store code");
        StoreValidation.requireNonNull(params.name(), "Store name");
        StoreValidation.requireNonNull(params.status(), "Store status");

        Store store = new Store(params.id(), params.code(), params.name(), params.status(),
                params.contactInfo(), params.address(), params.geolocation(), params.serviceSchedule());
        if (params.timeStamps() != null) {
            store.setTimeStamps(params.timeStamps());
        }
        logger.debug("Reconstructed store {} from persistent data", params.id().value());
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
        StoreValidation.requireNonNull(name, "StoreName");
        StoreValidation.requireNonNull(contactInfo, "ContactInfo");
        this.name = name;
        this.contactInfo = contactInfo;
        this.timeStamps.markAsUpdated();
    }

    public void relocate(Geolocation newGeolocation, Address newAddress) {
        StoreValidation.requireNonNull(newGeolocation, "Geolocation");
        StoreValidation.requireNonNull(newAddress, "Address");
        this.geolocation = newGeolocation;
        this.address = newAddress;
        this.timeStamps.markAsUpdated();
    }

    public void updateSchedule(StoreSchedule newSchedule) {
        StoreValidation.requireNonNull(newSchedule, "StoreSchedule");
        this.serviceSchedule = newSchedule;
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

    public void softDelete() {
        if (this.timeStamps == null) {
            this.timeStamps = StoreTimeStamps.now();
        }
        this.timeStamps.markAsDeleted();
        this.timeStamps.markAsUpdated();
    }

    public void restore() {
        if (this.timeStamps == null) {
            this.timeStamps = StoreTimeStamps.now();
        }
        this.timeStamps.restore();
        this.timeStamps.markAsUpdated();
    }

    public StoreID getId() {
        return id;
    }

    public StoreCode getCode() {
        return code;
    }

    public StoreName getName() {
        return name;
    }

    public StoreStatus getStatus() {
        return status;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public Address getAddress() {
        return address;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public StoreSchedule getServiceSchedule() {
        return serviceSchedule;
    }

    public StoreTimeStamps getTimeStamps() {
        return timeStamps;
    }
}