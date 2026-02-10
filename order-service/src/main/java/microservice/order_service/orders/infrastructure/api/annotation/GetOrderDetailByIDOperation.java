package microservice.order_service.orders.infrastructure.api.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import microservice.order_service.orders.infrastructure.api.dto.response.OrderDetailResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Get full purchaseOrder detail", description = "Returns extended information: items, address, tracking, detailed costs, and timestamps.")
@Parameters({
    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "PurchaseOrder UUID identifier", example = "c1d2e3f4-1111-2222-3333-abcdefabcdef")
})
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "PurchaseOrder detail found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDetailResponse.class), examples = @ExampleObject(name = "OrderDetailSuccess", value = """
        {
          "success": true,
          "code": 302,
          "message": "PurchaseOrder Detail",
          "data": {
            "id": "c1d2e3f4-1111-2222-3333-abcdefabcdef",
            "status": "SHIPPED",
            "deliveryMethod": "HOME_DELIVERY",
            "shippingCost": 150.00,
            "taxAmount": 24.00,
            "paymentId": "pay_987654321",
            "deliveryTrackingNumber": "TRK123456789",
            "items": [
              {
                "productId": "prod-123",
                "productName": "Vitamin C",
                "unitPrice": 99.90,
                "quantity": 2,
                "currency": "MXN",
                "isPrescriptionRequired": false
              }
            ],
            "address": {
              "id": "addr-123",
              "country": "Mexico",
              "state": "CDMX",
              "city": "Mexico City",
              "street": "Av Reforma 100",
              "zipCode": "01000"
            },
            "createdAt": "2025-01-09T08:00:00",
            "updatedAt": "2025-01-10T12:00:00"
          },
          "errors": null
        }
        """))),
    @ApiResponse(responseCode = "404", description = "Detail not found"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public @interface GetOrderDetailByIDOperation {
}
