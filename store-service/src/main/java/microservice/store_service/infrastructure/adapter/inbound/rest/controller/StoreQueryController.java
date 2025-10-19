package microservice.store_service.infrastructure.adapter.inbound.rest.controller;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageInput;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.store_service.application.StoreApplicationService;
import microservice.store_service.application.dto.query.GetStoreByCodeQuery;
import microservice.store_service.application.dto.query.GetStoreByIDQuery;
import microservice.store_service.application.dto.query.GetStoresByStatusQuery;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.model.enums.StoreStatus;
import microservice.store_service.infrastructure.adapter.inbound.rest.dto.request.SearchStoreRequest;
import microservice.store_service.infrastructure.adapter.inbound.rest.dto.response.StoreResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/stores")
public class StoreQueryController {
    private final StoreApplicationService storeApplicationFacade;
    private final ResponseMapper<StoreResponse, Store> responseMapper;

    @GetMapping("/{id}")
    public ResponseWrapper<StoreResponse> getStoreByID(@PathVariable String id) {
        var query = GetStoreByIDQuery.of(id);
        var queryResult = storeApplicationFacade.getStoreByID(query);

        var storeResponse = responseMapper.toResponse(queryResult);
        return ResponseWrapper.found(storeResponse, "Store");
    }

    @GetMapping("/{code}/code")
    public ResponseWrapper<StoreResponse> getStoreByCode(@PathVariable String code) {
        var query = GetStoreByCodeQuery.of(code);
        var queryResult = storeApplicationFacade.getStoreByCode(query);

        var storeResponse = responseMapper.toResponse(queryResult);
        return ResponseWrapper.found(storeResponse, "Store");
    }

    @GetMapping
    public ResponseWrapper<PageResponse<StoreResponse>> getStoresBySpecifications(@ModelAttribute SearchStoreRequest request) {
        var query = request.toQuery();
        var queryResult = storeApplicationFacade.searchStores(query);

        var storeResponses = responseMapper.toResponsePage(queryResult);
        return ResponseWrapper.found(storeResponses, "Stores by specifications");
    }


    @GetMapping("/status/{status}")
    public ResponseWrapper<PageResponse<StoreResponse>> getStoresByStatus(@PathVariable StoreStatus status, @ModelAttribute PageInput pagination) {
        var query = new GetStoresByStatusQuery(status, pagination.page(), pagination.size(), null);
        var queryResult = storeApplicationFacade.getStoresByStatus(query);

        var storeResponses = responseMapper.toResponsePage(queryResult);
        return ResponseWrapper.found(storeResponses, "Stores by status");
    }

}
