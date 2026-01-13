package microservice.store_service.app.application.port.in.command;

import lombok.Builder;
import microservice.store_service.app.application.port.in.command.valueobject.ContactInfoCommand;
import microservice.store_service.app.domain.model.valueobjects.StoreID;
import microservice.store_service.app.domain.model.valueobjects.StoreName;

@Builder
public record UpdateStoreNameAndContactCommand(
        StoreID id,
        StoreName name,
        ContactInfoCommand infoCommand
) {};