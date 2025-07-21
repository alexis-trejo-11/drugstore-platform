package microservice.ecommerce_cart_service.app.domain.entities.valueobjects;

import lombok.*;
import microservice.ecommerce_cart_service.app.domain.entities.CartItem;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AfterwardItem extends CartItem {
    public LocalDateTime addedAt;

    public static AfterwardItem from(CartItem cartItem) {
        AfterwardItem afterwardItem = new AfterwardItem();
        afterwardItem.setProductId(cartItem.getProductId());
        afterwardItem.setQuantity(cartItem.getQuantity());
        afterwardItem.setAddedAt(LocalDateTime.now());
        return afterwardItem;
    }
}
