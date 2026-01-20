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

  Optional<ProductModel> findByIdAndDeletedAtIsNull(String id);
  Optional<ProductModel> findByIdAndDeletedAtNotNull(String id);

  Optional<ProductModel> findByBarcodeAndDeletedAtIsNull(String barCode);

  @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProductModel p WHERE p.sku = :sku AND p.deletedAt IS NULL")
  boolean existsBySKU(@Param("sku") String sku);

  @Override
  @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProductModel p WHERE p.id = :id AND p.deletedAt IS NULL")
  boolean existsById(@Param("id") String id);

}
