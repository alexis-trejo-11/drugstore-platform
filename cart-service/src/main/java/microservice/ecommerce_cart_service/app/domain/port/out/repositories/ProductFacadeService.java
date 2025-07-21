package microservice.ecommerce_cart_service.app.domain.port.out.repositories;

import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.Product;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;

import java.util.List;
import java.util.Set;

public interface ProductFacadeService {
    List<Product> findAvailableByIdIn(Set<ProductId> productIds);
    List<Product> findByIdIn(List<ProductId> productIds);
}