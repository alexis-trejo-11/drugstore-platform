package microservice.store_service.application.query;

import jakarta.validation.constraints.NotBlank;
import microservice.store_service.domain.model.enums.StoreStatus;
import microservice.store_service.domain.specification.StoreSearchCriteria;
import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.data.domain.Pageable;

public record GetStoresByStatusQuery(
        @NotNull @NotBlank StoreStatus status,
        @NotNull Integer page,
        @NotNull Integer size,
        StoreSearchCriteria.SortCriteria sortCriteria) {}
