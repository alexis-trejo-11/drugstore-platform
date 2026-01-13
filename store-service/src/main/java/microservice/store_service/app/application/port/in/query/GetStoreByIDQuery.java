package microservice.store_service.app.application.port.in.query;

import lombok.Builder;
import microservice.store_service.app.domain.model.valueobjects.StoreID;
import org.codehaus.commons.nullanalysis.NotNull;

@Builder
public record GetStoreByIDQuery(@NotNull StoreID id) {
    public static GetStoreByIDQuery of(@NotNull String id) {
        return new GetStoreByIDQuery(new StoreID(id));
    }
}