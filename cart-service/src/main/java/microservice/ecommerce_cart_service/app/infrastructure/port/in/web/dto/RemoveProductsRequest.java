package microservice.ecommerce_cart_service.app.infrastructure.port.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveProductsRequest {
    private Set<UUID> productUUIDs;
}
