package org.github.alexisTrejo11.drugstore.stores.application.port.in.command;

import lombok.Builder;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject.ContactInfoCommand;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreName;

@Builder
public record UpdateStoreNameAndContactCommand(
        StoreID id,
        StoreName name,
        ContactInfoCommand infoCommand
) {};