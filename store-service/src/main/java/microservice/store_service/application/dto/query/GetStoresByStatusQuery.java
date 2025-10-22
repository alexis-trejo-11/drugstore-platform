package microservice.store_service.application.dto.query;

import jakarta.validation.constraints.NotBlank;
import libs_kernel.page.Pagination;
import microservice.store_service.domain.model.enums.StoreStatus;
import microservice.store_service.domain.specification.StoreSearchCriteria;
import org.codehaus.commons.nullanalysis.NotNull;

public record GetStoresByStatusQuery(
        @NotNull @NotBlank StoreStatus status,
        Pagination pagination,
        StoreSearchCriteria.SortCriteria sortCriteria) {}
