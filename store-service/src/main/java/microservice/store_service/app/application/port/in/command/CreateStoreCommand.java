package microservice.store_service.app.application.port.in.command;

import lombok.Builder;
import microservice.store_service.app.application.port.in.command.valueobject.AddressCommand;
import microservice.store_service.app.application.port.in.command.valueobject.ContactInfoCommand;
import microservice.store_service.app.application.port.in.command.valueobject.GeolocationCommand;
import microservice.store_service.app.domain.model.CreateStoreParams;
import microservice.store_service.app.domain.model.enums.StoreStatus;
import microservice.store_service.app.domain.model.valueobjects.StoreCode;
import microservice.store_service.app.domain.model.valueobjects.StoreName;

@Builder
public record CreateStoreCommand(
        StoreCode code,
        StoreName name,
        StoreStatus status,
        AddressCommand addressCommand,
        ContactInfoCommand infoCommand,
        OrderScheduleCommand scheduleCommand,
        GeolocationCommand geoCommand
) {

	public CreateStoreParams toCreateStoreParams() {
				return new CreateStoreParams(
								this.code,
								this.name,
								this.infoCommand != null ? this.infoCommand.toContactInfo() : null,
								this.addressCommand != null ? this.addressCommand.toAddress() : null,
								this.geoCommand != null ? this.geoCommand.toGeolocation() : null,
								this.scheduleCommand != null ? this.scheduleCommand.toDomain() : null
				);


	}
};