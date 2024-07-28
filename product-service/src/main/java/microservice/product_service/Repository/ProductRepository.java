package microservice.product_service.Repository;

import microservice.product_service.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory_Id(Long categoryId);

    List<Product> findBySubcategory_Id(Long subcategoryId);

    List<Product> findBySupplier_Id(Long supplierId);

    List<Product> findByIdIn(List<Long> productIds);


}

