package microservice.store_service.infrastructure.adapter.inbound.rest.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import libs_kernel.page.PageableResponse;
import microservice.store_service.domain.model.Store;
import microservice.store_service.infrastructure.adapter.inbound.rest.dto.response.StoreResponse;
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
        PageableResponse<StoreResponse> pageResponse = new PageableResponse<>();
        if (stores == null) return pageResponse;

        Page<StoreResponse> responsePage = stores.map(this::toResponse);
        return pageResponse.fromPage(responsePage);
    }
}
