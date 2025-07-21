package microservice.ecommerce_cart_service.app.infrastructure.port.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAfterwardsRequest {
    @NotNull(message = "Cart ID cannot be null")
    private UUID cartId;

    @NotNull(message = "Product IDs cannot be null")
    private Set<UUID> productIdSet;
}
