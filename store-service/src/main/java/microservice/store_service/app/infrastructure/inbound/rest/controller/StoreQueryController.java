package microservice.store_service.app.infrastructure.inbound.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import microservice.store_service.app.application.port.in.query.GetStoreByCodeQuery;
import microservice.store_service.app.application.port.in.query.GetStoreByIDQuery;
import microservice.store_service.app.application.port.in.query.GetStoresByStatusQuery;
import microservice.store_service.app.application.port.in.usecase.StoreQueryUseCases;
import microservice.store_service.app.domain.model.Store;
import microservice.store_service.app.domain.model.enums.StoreStatus;
import microservice.store_service.app.infrastructure.inbound.rest.annotation.*;
import microservice.store_service.app.infrastructure.inbound.rest.dto.request.SearchStoreRequest;
import microservice.store_service.app.infrastructure.inbound.rest.dto.response.StoreResponse;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v2/stores")
@Tag(
    name = "Store Query Operations",
    description = "Endpoints for querying and retrieving store information. These operations allow searching, filtering, and retrieving store data based on various criteria."
)
@SecurityRequirement(name = "bearerAuth")
public class StoreQueryController {
    private final StoreQueryUseCases storeApplicationFacade;
    private final ResponseMapper<StoreResponse, Store> responseMapper;

    @Autowired
    public StoreQueryController(
        StoreQueryUseCases storeApplicationFacade,
        ResponseMapper<StoreResponse, Store> responseMapper) {
        this.storeApplicationFacade = storeApplicationFacade;
        this.responseMapper = responseMapper;
    }

    @GetMapping("/{id}")
    @RateLimit(profile = RateLimitProfile.PUBLIC)
    @GetStoreByIDOperation
    public ResponseWrapper<StoreResponse> getStoreByID(
        @StoreIdURLParameter @PathVariable String id
    ) {
        var query = GetStoreByIDQuery.of(id);
        var queryResult = storeApplicationFacade.getStoreByID(query);

        var storeResponse = responseMapper.toResponse(queryResult);
        return ResponseWrapper.found(storeResponse, "Store");
    }

    @GetMapping("/by-code/{code}")
    @RateLimit(profile = RateLimitProfile.PUBLIC)
    @GetStoreByCodeOperation
    public ResponseWrapper<StoreResponse> getStoreByCode(
        @Parameter(description = "Unique business code of the store", required = true, example = "STR-001")
        @PathVariable String code
    ) {
        var query = GetStoreByCodeQuery.of(code);
        var queryResult = storeApplicationFacade.getStoreByCode(query);

        var storeResponse = responseMapper.toResponse(queryResult);
        return ResponseWrapper.found(storeResponse, "Store");
    }

    @GetMapping
    @RateLimit(profile = RateLimitProfile.PUBLIC)
    @GetStoresBySpecificationsOperation
    public ResponseWrapper<PageResponse<StoreResponse>> getStoresBySpecifications(
        @Parameter(
            description = "Search criteria for filtering stores",
            required = false,
            schema = @Schema(implementation = SearchStoreRequest.class)
        )
        @ModelAttribute SearchStoreRequest request
    ) {
        var query = request.toQuery();
        var storesPage = storeApplicationFacade.searchStores(query);

        var storePageResponse = responseMapper.toResponsePage(storesPage);
        return ResponseWrapper.found(storePageResponse, "Stores by specifications");
    }


    @GetMapping("/status/{status}")
    @RateLimit(profile = RateLimitProfile.PUBLIC)
    @GetStoresByStatusOperation
    public ResponseWrapper<PageResponse<StoreResponse>> getStoresByStatus(
        @Parameter(
            description = "Store status to filter by",
            required = true,
            example = "ACTIVE",
            schema = @Schema(implementation = StoreStatus.class)
        )
        @PathVariable StoreStatus status,
        @Parameter(
            description = "Pagination parameters (page number and size)",
            required = false,
            schema = @Schema(implementation = PageRequest.class)
        )
        @ModelAttribute PageRequest pagination
    ) {
        pagination = pagination == null ? PageRequest.defaultPageRequest() : pagination;
        var query = new GetStoresByStatusQuery(status, pagination.toPageable(), null);
        var storesPage = storeApplicationFacade.getStoresByStatus(query);

        var storePageResponse = responseMapper.toResponsePage(storesPage);
        return ResponseWrapper.found(storePageResponse, "Stores by status");
    }

}
