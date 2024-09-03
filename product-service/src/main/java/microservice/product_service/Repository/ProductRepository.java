package microservice.product_service.Repository;

import microservice.product_service.Model.MainCategory;
import microservice.product_service.Model.Product;
import microservice.product_service.Model.Subcategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIdIn(List<Long> productIds);

    @Query("SELECT p FROM Product p " +
            "JOIN p.mainCategory mc " +
            "JOIN p.category c " +
            "JOIN p.subcategory sc " +
            "ORDER BY mc.name ASC, c.name ASC, sc.name ASC")
    Page<Product> findAllSortedByCategoryHierarchy(Pageable pageable);
    
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
    Page<Product> findBySubcategory_Id(Long subcategoryId, Pageable pageable);
    Page<Product> findBySupplier_Id(Long supplierId, Pageable pageable);
    Page<Product> findByMainCategory(MainCategory mainCategory, Pageable pageable);
    Page<Product> findBySubcategory(Subcategory mainCategory, Pageable pageable);

}

