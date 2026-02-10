package microservice.order_service.orders.infrastructure.api.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import libs_kernel.page.PageResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Search purchaseOrders (paginated and filtered)", description = "Performs a paginated search of purchaseOrders applying dynamic filters.\n"
    + "Typical parameters supported in OrderSearchRequest: status, deliveryMethod, dateFrom, dateTo, userId, page, size, sort.\n"
    + "Returns a standard ResponseWrapper container with pagination metadata.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class), examples = @ExampleObject(name = "SearchOrdersSuccess", summary = "Example of purchaseOrders page", value = """
        {
          "success": true,
          "code": 200,
          "message": "Orders found successfully",
          "data": {
            "content": [
              {
                "id": "c1d2e3f4-1111-2222-3333-abcdefabcdef",
                "status": "PENDING",
                "deliveryMethod": "HOME_DELIVERY",
                "shippingCost": 120.50,
                "taxAmount": 19.28,
                "createdAt": "2025-01-15T10:15:30",
                "updatedAt": "2025-01-15T11:00:00"
              }
            ],
            "page": 0,
            "size": 20,
            "totalElements": 1,
            "totalPages": 1,
            "hasNext": false,
            "hasPrevious": false,
            "sort": "createdAt,DESC"
          },
          "errors": null
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Invalid filter parameters", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "SearchOrdersBadRequest", value = """
        {
          "success": false,
          "code": 400,
          "message": "Invalid search criteria",
          "data": null,
          "errors": {
            "status": "Unsupported status value"
          }
        }
        """))),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
})
public @interface SearchOrdersOperation {
}
