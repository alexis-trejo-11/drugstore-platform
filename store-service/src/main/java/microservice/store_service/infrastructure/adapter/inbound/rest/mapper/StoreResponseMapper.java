package microservice.store_service.infrastructure.adapter.inbound.rest.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import libs_kernel.page.PageableResponse;
import microservice.store_service.application.handler.result.StoreQueryResult;
import microservice.store_service.infrastructure.adapter.inbound.rest.dto.response.StoreResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StoreResponseMapper implements ResponseMapper<StoreResponse, StoreQueryResult> {
    @Override
    public StoreResponse toResponse(StoreQueryResult storeQueryResult) {
        return StoreResponse.builder()
                .id(storeQueryResult.id().value())
                .code(storeQueryResult.code().value())
                .name(storeQueryResult.name().value())
                .status(storeQueryResult.status().name())
                .createdAt(storeQueryResult.createdAt())
                .updatedAt(storeQueryResult.updatedAt())
                .build();
    }

    @Override
    public List<StoreResponse> toResponses(List<StoreQueryResult> storeQueryResults) {
        return storeQueryResults.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PageResponse<StoreResponse> toResponsePage(Page<StoreQueryResult> storeQueryResults) {
        PageableResponse<StoreResponse> pageResponse = new PageableResponse<>();
        if (storeQueryResults == null) return pageResponse;

        Page<StoreResponse> responsePage = storeQueryResults.map(this::toResponse);
        return pageResponse.fromPage(responsePage);
    }
}
