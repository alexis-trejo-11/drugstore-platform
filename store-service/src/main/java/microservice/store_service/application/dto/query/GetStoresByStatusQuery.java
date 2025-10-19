package microservice.store_service.application.dto.query;

import jakarta.validation.constraints.NotBlank;
import libs_kernel.page.PageInput;
import microservice.store_service.domain.model.enums.StoreStatus;
import microservice.store_service.domain.specification.StoreSearchCriteria;
import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.data.domain.Pageable;

public record GetStoresByStatusQuery(
        @NotNull @NotBlank StoreStatus status,
        PageInput pagination,
        StoreSearchCriteria.SortCriteria sortCriteria) {}
