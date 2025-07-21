package microservice.ecommerce_cart_service.app.application.command.afterwards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;
import microservice.ecommerce_cart_service.app.infrastructure.port.in.web.dto.DeleteAfterwardsRequest;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveAfterwardsCommand {
    private CartId cartId;
    private List<ProductId> productIdSet;


    public static RemoveAfterwardsCommand from(DeleteAfterwardsRequest request) {
        CartId cartId= new CartId(request.getCartId());
        List<ProductId> productIds = request.getProductIdSet().stream()
                .map(ProductId::new)
                .toList();

        return new RemoveAfterwardsCommand(cartId, productIds);
    }
}
