package microservice.store_service.app.infrastructure.inbound.rest.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import microservice.store_service.app.domain.model.Store;
import microservice.store_service.app.infrastructure.inbound.rest.dto.response.StoreResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StoreResponseMapper implements ResponseMapper<StoreResponse, Store> {
	@Override
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

	@Override
	public List<StoreResponse> toResponses(List<Store> stores) {
		return stores.stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public PageResponse<StoreResponse> toResponsePage(Page<Store> stores) {
		if (stores == null) return null;

		return PageResponse.from(stores.map(this::toResponse));
	}
}
