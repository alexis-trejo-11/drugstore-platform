package microservice.order_service.orders.infrastructure.api.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import microservice.order_service.orders.infrastructure.api.dto.request.CreateOrderRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@RequestBody(required = true, description = "PurchaseOrder creation payload", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateOrderRequest.class), examples = @ExampleObject(name = "CreateOrderRequest", value = """
    {
      "userId": "b2a11111-2222-3333-4444-555566667777",
      "deliveryMethod": "HOME_DELIVERY",
      "addressId": "addr-123",
      "notes": "Leave at front desk",
      "items": [
        {
          "productId": "prod-123",
          "productName": "Vitamin C",
          "unitPrice": 99.90,
          "quantity": 2,
          "currency": "MXN",
          "isPrescriptionRequired": false
        }
      ]
    }
    """)))
public @interface CreateOrderRequestBody {
}
