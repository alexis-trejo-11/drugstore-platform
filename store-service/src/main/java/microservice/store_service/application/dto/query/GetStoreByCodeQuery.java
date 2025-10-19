package microservice.store_service.application.dto.query;

import jakarta.validation.constraints.NotBlank;
import microservice.store_service.domain.model.valueobjects.StoreCode;
import org.codehaus.commons.nullanalysis.NotNull;

public record GetStoreByCodeQuery(@NotNull StoreCode code) {
    public static GetStoreByCodeQuery of(@NotBlank String code) {
        return new GetStoreByCodeQuery(new StoreCode(code));
    }
}