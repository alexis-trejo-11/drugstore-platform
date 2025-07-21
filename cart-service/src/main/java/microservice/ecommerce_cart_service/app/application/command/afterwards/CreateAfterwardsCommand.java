package microservice.ecommerce_cart_service.app.application.command.afterwards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;
import microservice.ecommerce_cart_service.app.infrastructure.port.in.web.dto.CreateAfterwardsRequest;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAfterwardsCommand {
    private List<ProductId> productId;
    private CartId cartId;

    public static CreateAfterwardsCommand fromRequest(CreateAfterwardsRequest request) {
        List<ProductId> productIdList = request.getProductIds().stream()
                .map(ProductId::new)
                .toList();
        CartId cartId = new CartId(request.getCartId());

        return new CreateAfterwardsCommand(productIdList,cartId);
    }
}
