package io.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.mapper;

import libs_kernel.page.PageResponse;
import io.github.alexisTrejo11.drugstore.stores.domain.model.Store;
import io.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.response.StoreResponse;
import libs_kernel.page.PaginationMetadata;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StoreResponseMapper {

	public StoreResponse toResponse(Store store) {
		if (store == null) return null;
		var createdAt = store.getTimeStamps() != null ? store.getTimeStamps().getCreatedAt() : null;
		var updatedAt = store.getTimeStamps() != null ? store.getTimeStamps().getUpdatedAt() : null;

		return StoreResponse.builder()
				.id(store.getId().value())
				.code(store.getCode().value())
				.name(store.getName().value())
				.status(store.getStatus().name())
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.build();
	}

	public List<StoreResponse> toResponses(List<Store> stores) {
		return stores.stream()
				.map(this::toResponse)
				.toList();
	}

	public PageResponse<StoreResponse> toResponsePage(Page<Store> stores) {
		if (stores == null) return null;

		var paginationMetadata = new PaginationMetadata(stores.getNumber(), stores.getSize(), stores.getTotalPages());
		var storeResponses =  stores.map(this::toResponse).stream().toList();
		return new PageResponse<>(storeResponses, paginationMetadata);
	}
}
