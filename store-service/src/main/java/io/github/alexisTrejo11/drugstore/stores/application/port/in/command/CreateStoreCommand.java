package io.github.alexisTrejo11.drugstore.stores.application.port.in.command;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.AddressCommand;
import io.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.ContactInfoCommand;
import io.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.GeolocationCommand;
import io.github.alexisTrejo11.drugstore.stores.domain.model.CreateStoreParams;
import io.github.alexisTrejo11.drugstore.stores.domain.model.enums.StoreStatus;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreName;

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