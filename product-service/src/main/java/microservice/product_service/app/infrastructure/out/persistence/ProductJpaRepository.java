package microservice.product_service.app.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductJpaRepository
    extends JpaRepository<ProductModel, String>, JpaSpecificationExecutor<ProductModel> {

  @Query("SELECT p FROM ProductModel p WHERE p.id = :id")
  Optional<ProductModel> findByIdIncludeDeleted(@Param("id") String id);

  boolean existsByCode(String code);
}
