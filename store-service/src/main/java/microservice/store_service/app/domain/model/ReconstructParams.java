package microservice.store_service.app.domain.model;

import microservice.store_service.app.domain.model.schedule.StoreSchedule;
import microservice.store_service.app.domain.model.valueobjects.*;
import microservice.store_service.app.domain.model.valueobjects.location.Address;
import microservice.store_service.app.domain.model.valueobjects.location.Geolocation;
import microservice.store_service.app.domain.model.enums.StoreStatus;

public record ReconstructParams(
        StoreID id,
        StoreCode code,
        StoreName name,
        StoreStatus status,
        ContactInfo contactInfo,
        Address address,
        Geolocation geolocation,
        StoreSchedule serviceSchedule,
        StoreTimeStamps timeStamps
) {}
