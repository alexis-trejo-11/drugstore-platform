package microservice.store_service.application.command;

import lombok.Builder;
import microservice.store_service.application.command.valueobject.ContactInfoCommand;
import microservice.store_service.domain.model.valueobjects.StoreID;
import microservice.store_service.domain.model.valueobjects.StoreName;

@Builder
public record UpdateStoreNameAndContactCommand(
        StoreID id,
        StoreName name,
        ContactInfoCommand infoCommand
) {};