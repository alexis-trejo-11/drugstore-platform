package microservice.store_service.app.application.port.in.query;

import jakarta.validation.constraints.NotBlank;
import microservice.store_service.app.domain.model.valueobjects.StoreCode;
import org.codehaus.commons.nullanalysis.NotNull;

public record GetStoreByCodeQuery(@NotNull StoreCode code) {
    public static GetStoreByCodeQuery of(@NotBlank String code) {
        return new GetStoreByCodeQuery(new StoreCode(code));
    }
}