package microservice.store_service.app.application.port.in.query;

import jakarta.validation.constraints.NotBlank;
import microservice.store_service.app.domain.model.enums.StoreStatus;
import microservice.store_service.app.domain.specification.StoreSearchCriteria;
import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.data.domain.Pageable;

public record GetStoresByStatusQuery(
        @NotNull @NotBlank StoreStatus status,
        Pageable pagination,
        StoreSearchCriteria.SortCriteria sortCriteria) {

	public GetStoresByStatusQuery {
		if (sortCriteria == null) {
			sortCriteria = StoreSearchCriteria.SortCriteria.CREATED_AT_DESC;
		}

		if (pagination == null) {
			pagination = Pageable.unpaged();
		}

		if (status == null) {
			throw new IllegalArgumentException("Status must not be null");
		}
	}
}
