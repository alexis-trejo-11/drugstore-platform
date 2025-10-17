package microservice.store_service.application.query.result;

import lombok.Builder;
import microservice.store_service.domain.model.enums.StoreStatus;
import microservice.store_service.domain.model.valueobjects.*;

import java.time.LocalDateTime;

@Builder
public record StoreQueryResult(
        StoreID id,
        String code,
        String name,
        ContactInfo contactInfo,
        StoreStatus status,
        Address address,
        ServiceSchedule serviceSchedule,
        Geolocation geolocation,
        boolean is24Hours,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
