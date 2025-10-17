package microservice.store_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import microservice.store_service.domain.exception.StoreBusinessRuleException;
import microservice.store_service.domain.exception.StoreConflictException;
import microservice.store_service.domain.model.enums.StoreStatus;
import microservice.store_service.domain.model.valueobjects.*;

@Builder
@AllArgsConstructor
@Getter
public class Store {
    private StoreID id;
    private String code;
    private String name;
    private boolean is24Hours;

    private StoreStatus status;
    private ContactInfo contactInfo;
    private Address address;
    private Geolocation geolocation;
    private ServiceSchedule serviceSchedule;
    private StoreTimeStamps timeStamps;

    public void activate() {
        this.status = StoreStatus.ACTIVE;
        this.timeStamps.updateTimestamp();
    }

    public void deactivate() {
        this.status = StoreStatus.INACTIVE;
        this.timeStamps.updateTimestamp();
    }

    public void updateInformation(String name, ContactInfo contactInfo, Address address) {
        this.name = name;
        this.address = address;
        this.contactInfo = contactInfo;
        this.timeStamps.updateTimestamp();
    }

    public void updateLocation(Geolocation geolocation) {
        geolocation.validate();
        this.geolocation = geolocation;
        this.timeStamps.updateTimestamp();
    }

    public void updateSchedule(boolean is24Hours, ServiceSchedule serviceSchedule) {
        this.is24Hours = is24Hours;
        if (is24Hours) {
            this.serviceSchedule = ServiceSchedule.get24HoursSchedule();
        } else {
            serviceSchedule.validate();
            this.serviceSchedule = serviceSchedule;
        }

        this.timeStamps.updateTimestamp();
    }

    public void validatePersist() {
        validateRequiredProperties();
        geolocation.validate();
        address.validate();
        serviceSchedule.validateScheduleWithinDefaultHours(is24Hours);
    }

    public void validateRequiredProperties() {
        if (this.code == null || this.code.isBlank()) {
            throw new StoreBusinessRuleException("Store code is required.");
        }
        if (this.name == null || this.name.isBlank()) {
            throw new StoreBusinessRuleException("Store name is required.");
        }
        if (this.contactInfo == null) {
            throw new StoreBusinessRuleException("Contact information is required.");
        }
        if (this.status == null) {
            throw new StoreBusinessRuleException("Store status is required.");
        }
        if (this.geolocation == null) {
            throw new StoreBusinessRuleException("Store geolocation is required.");
        }
        if (this.address == null) {
            throw new StoreBusinessRuleException("Store address is required.");
        }
    }

    public void assignID() {
        if (this.id != null) {
            throw new  StoreConflictException("Store ID is already assigned.");
        }

        this.id = StoreID.generate();
    }
}
