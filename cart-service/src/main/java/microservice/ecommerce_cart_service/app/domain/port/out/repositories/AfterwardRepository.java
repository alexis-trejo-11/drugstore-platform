package microservice.ecommerce_cart_service.app.domain.port.out.repositories;

import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.AfterwardItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;
import microservice.ecommerce_cart_service.app.shared.QueryPageData;

import java.util.List;

public interface AfterwardRepository {
    List<AfterwardItem> findAllByCartId(CartId customerId, QueryPageData pageData);
    List<AfterwardItem> findAllByCartId(CartId customerId);
    void bulkSave(List<AfterwardItem> afterwardItems);
    void bulkDelete(List<AfterwardItem> afterwardItems);
}
