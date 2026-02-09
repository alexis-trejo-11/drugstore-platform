package microservice.store_service.app.infrastructure.inbound.rest.annotation;

import io.swagger.v3.oas.annotations.Parameter;

@Parameter(description = "Store unique identifier", required = true, example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890")
public @interface StoreIdURLParameter {
}
