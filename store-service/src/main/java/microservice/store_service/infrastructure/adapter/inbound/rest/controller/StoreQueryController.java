package microservice.store_service.infrastructure.adapter.inbound.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.Pagination;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.store_service.domain.port.input.StoreApplicationService;
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
@Tag(
    name = "Store Query Operations",
    description = "Endpoints for querying and retrieving store information. These operations allow searching, filtering, and retrieving store data based on various criteria."
)
@SecurityRequirement(name = "bearerAuth")
public class StoreQueryController {
    private final StoreApplicationService storeApplicationFacade;
    private final ResponseMapper<StoreResponse, Store> responseMapper;

    @GetMapping("/{id}")
    @Operation(
        summary = "Get store by ID",
        description = "Retrieves a single store by its unique identifier. **Requires JWT authentication with ADMIN role.**",
        tags = {"Store Query Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Store found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseWrapper.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Store found successfully",
                      "data": {
                        "id": "c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                        "code": "STR-001",
                        "name": "Central Pharmacy",
                        "status": "ACTIVE",
                        "phone": "+1-555-0123",
                        "email": "central@pharmacy.com",
                        "address": {
                          "street": "123 Main St",
                          "city": "New York",
                          "state": "NY",
                          "zipCode": "10001",
                          "country": "USA"
                        },
                        "latitude": 40.7128,
                        "longitude": -74.0060,
                        "isOpen": true,
                        "createdAt": "2025-01-15T10:30:00",
                        "updatedAt": "2025-10-19T14:20:00"
                      },
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Store not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": false,
                      "message": "Store not found",
                      "timestamp": "2025-10-19T14:25:30",
                      "error": {
                        "errorCode": "STORE_NOT_FOUND",
                        "message": "No store found with ID: c1a2b3d4-e5f6-7890-abcd-ef1234567890"
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": false,
                      "message": "Unauthorized access",
                      "timestamp": "2025-10-19T14:25:30",
                      "error": {
                        "errorCode": "UNAUTHORIZED",
                        "message": "Valid JWT token required"
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions (ADMIN role required)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": false,
                      "message": "Forbidden access",
                      "timestamp": "2025-10-19T14:25:30",
                      "error": {
                        "errorCode": "FORBIDDEN",
                        "message": "ADMIN role required to access this resource"
                      }
                    }
                    """
                )
            )
        )
    })
    public ResponseWrapper<StoreResponse> getStoreByID(
        @Parameter(description = "Unique identifier of the store", required = true, example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable String id
    ) {
        var query = GetStoreByIDQuery.of(id);
        var queryResult = storeApplicationFacade.getStoreByID(query);

        var storeResponse = responseMapper.toResponse(queryResult);
        return ResponseWrapper.found(storeResponse, "Store");
    }

    @GetMapping("/{code}/code")
    @Operation(
        summary = "Get store by code",
        description = "Retrieves a single store by its unique business code. **Requires JWT authentication with ADMIN role.**",
        tags = {"Store Query Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Store found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseWrapper.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Store found successfully",
                      "data": {
                        "id": "c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                        "code": "STR-001",
                        "name": "Central Pharmacy",
                        "status": "ACTIVE",
                        "phone": "+1-555-0123",
                        "email": "central@pharmacy.com",
                        "isOpen": true
                      },
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Store not found with the provided code",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(mediaType = "application/json")
        )
    })
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
    @Operation(
        summary = "Search stores by specifications",
        description = """
            Advanced search endpoint that allows filtering stores by multiple criteria including:
            - Name, phone, email (partial match)
            - Code (exact match)
            - Geographic location (country, state, neighborhood)
            - Status
            - Location filters (proximity search, geographic boundaries)
            - Schedule filters (24-hour stores, currently open, specific day/time)
            
            **Requires JWT authentication with ADMIN role.**
            
            Supports pagination and sorting.
            """,
        tags = {"Store Query Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Stores retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseWrapper.class),
                examples = @ExampleObject(
                    name = "Paginated Success Response",
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Stores by specifications found successfully",
                      "data": {
                        "content": [
                          {
                            "id": "c1a2b3d4",
                            "code": "STR-001",
                            "name": "Central Pharmacy",
                            "status": "ACTIVE",
                            "phone": "+1-555-0123",
                            "email": "central@pharmacy.com",
                            "isOpen": true
                          }
                        ],
                        "page": 0,
                        "size": 10,
                        "totalElements": 25,
                        "totalPages": 3,
                        "first": true,
                        "last": false,
                        "hasNext": true,
                        "hasPrevious": false
                      },
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid search parameters",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "isSuccess": false,
                      "message": "Bad request",
                      "timestamp": "2025-10-19T14:25:30",
                      "error": {
                        "errorCode": "INVALID_PARAMETERS",
                        "message": "Invalid pagination parameters"
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(mediaType = "application/json")
        )
    })
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
    @Operation(
        summary = "Get stores by status",
        description = """
            Retrieves all stores with a specific status (ACTIVE, INACTIVE, UNDER_MAINTENANCE, TEMPORARY_CLOSURE).
            
            **Requires JWT authentication with ADMIN role.**
            
            Returns paginated results with configurable page size and sorting.
            """,
        tags = {"Store Query Operations"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Stores retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseWrapper.class),
                examples = @ExampleObject(
                    name = "Active Stores Response",
                    value = """
                    {
                      "isSuccess": true,
                      "message": "Stores by status found successfully",
                      "data": {
                        "content": [
                          {
                            "id": "c1a2b3d4",
                            "code": "STR-001",
                            "name": "Central Pharmacy",
                            "status": "ACTIVE",
                            "isOpen": true
                          },
                          {
                            "id": "e5f6g7h8",
                            "code": "STR-002",
                            "name": "Downtown Drugstore",
                            "status": "ACTIVE",
                            "isOpen": false
                          }
                        ],
                        "page": 0,
                        "size": 10,
                        "totalElements": 42,
                        "totalPages": 5,
                        "first": true,
                        "last": false,
                        "hasNext": true,
                        "hasPrevious": false
                      },
                      "timestamp": "2025-10-19T14:25:30"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid status value",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(mediaType = "application/json")
        )
    })
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
            schema = @Schema(implementation = Pagination.class)
        )
        @ModelAttribute Pagination pagination
    ) {
        pagination = pagination == null ? Pagination.defaultPageInput() : pagination;
        var query = new GetStoresByStatusQuery(status, pagination, null);
        var storesPage = storeApplicationFacade.getStoresByStatus(query);

        var storePageResponse = responseMapper.toResponsePage(storesPage);
        return ResponseWrapper.found(storePageResponse, "Stores by status");
    }

}
