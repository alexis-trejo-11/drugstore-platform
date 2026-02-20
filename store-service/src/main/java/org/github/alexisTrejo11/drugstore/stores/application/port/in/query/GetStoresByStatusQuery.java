package org.github.alexisTrejo11.drugstore.stores.application.port.in.query;

import jakarta.validation.constraints.NotBlank;
import org.github.alexisTrejo11.drugstore.stores.domain.model.enums.StoreStatus;
import org.github.alexisTrejo11.drugstore.stores.domain.specification.StoreSearchCriteria;
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
