package microservice.store_service.application.handler.result;

import lombok.Builder;
import microservice.store_service.domain.model.enums.StoreStatus;
import microservice.store_service.domain.model.valueobjects.*;
import microservice.store_service.domain.model.valueobjects.location.Address;
import microservice.store_service.domain.model.valueobjects.ContactInfo;
import microservice.store_service.domain.model.valueobjects.location.Geolocation;
import microservice.store_service.domain.model.schedule.StoreSchedule;

import java.time.LocalDateTime;

@Builder
public record StoreQueryResult(
        StoreID id,
        StoreCode code,
        StoreName name,
        ContactInfo contactInfo,
        StoreStatus status,
        Address address,
        StoreSchedule serviceSchedule,
        Geolocation geolocation,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
