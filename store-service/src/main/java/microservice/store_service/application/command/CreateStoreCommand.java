package microservice.store_service.application.command;

import lombok.Builder;
import microservice.store_service.domain.model.enums.StoreStatus;

@Builder
public record CreateStoreCommand(
        String code,
        String name,
        StoreStatus status,
        boolean is24Hours,
        AddressCommand addressCommand,
        ContactInfoCommand infoCommand,
        ServiceScheduleCommand scheduleCommand,
        GeolocationCommand geoCommand
) {};