package microservice.cart_service.app.product.adapter.output.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductJpaRepository extends JpaRepository<ProductModel, String> {

  @Query("SELECT p FROM ProductModel p WHERE p.id IN :productIds AND p.isAvailable = true")
  List<ProductModel> findAvailableByIdIn(@Param("productIds") List<String> productIds);
}
