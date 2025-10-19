package microservice.store_service.application.dto.command;

import lombok.Builder;
import microservice.store_service.application.dto.command.valueobject.ContactInfoCommand;
import microservice.store_service.domain.model.valueobjects.StoreID;
import microservice.store_service.domain.model.valueobjects.StoreName;

@Builder
public record UpdateStoreNameAndContactCommand(
        StoreID id,
        StoreName name,
        ContactInfoCommand infoCommand
) {};