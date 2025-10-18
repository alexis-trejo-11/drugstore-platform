package microservice.store_service.application.command;

import lombok.Builder;
import microservice.store_service.application.command.valueobject.AddressCommand;
import microservice.store_service.application.command.valueobject.ContactInfoCommand;
import microservice.store_service.application.command.valueobject.GeolocationCommand;
import microservice.store_service.application.command.valueobject.ScheduleCommand;
import microservice.store_service.domain.model.enums.StoreStatus;
import microservice.store_service.domain.model.valueobjects.StoreCode;
import microservice.store_service.domain.model.valueobjects.StoreName;

@Builder
public record CreateStoreCommand(
        StoreCode code,
        StoreName name,
        StoreStatus status,
        AddressCommand addressCommand,
        ContactInfoCommand infoCommand,
        ScheduleCommand scheduleCommand,
        GeolocationCommand geoCommand
) {};