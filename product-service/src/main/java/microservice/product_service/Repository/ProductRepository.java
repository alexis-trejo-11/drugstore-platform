package microservice.product_service.Repository;

import microservice.product_service.Model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p.category FROM Product p")
    List<Category> findAllCategories();

    @Query("SELECT DISTINCT p.mainCategory FROM Product p")
    List<MainCategory> findAllMainCategories();

    @Query("SELECT DISTINCT p.subcategory FROM Product p")
    List<Subcategory> findAllSubcategories();

    @Query("SELECT DISTINCT p.supplier FROM Product p")
    List<Supplier> findAllSuppliers();

    List<Product> findByIdIn(List<Long> productIds);

    @Query("SELECT p FROM Product p " +
            "JOIN p.mainCategory mc " +
            "JOIN p.category c " +
            "JOIN p.subcategory sc " +
            "ORDER BY mc.name ASC, c.name ASC, sc.name ASC")
    Page<Product> findAllSortedByCategoryHierarchy(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.category " +
            "WHERE p.category.id = :categoryId")
    Page<Product> findByCategory_Id(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.subcategory " +
            "WHERE p.subcategory.id = :subcategoryId")
    Page<Product> findBySubcategory_Id(@Param("subcategoryId") Long subcategoryId, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.supplier " +
            "WHERE p.supplier.id = :supplierId")
    Page<Product> findBySupplier_Id(@Param("supplierId") Long supplierId, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.mainCategory " +
            "WHERE p.mainCategory = :mainCategory")
    Page<Product> findByMainCategory(@Param("mainCategory") MainCategory mainCategory, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.subcategory " +
            "WHERE p.subcategory = :subcategory")
    Page<Product> findBySubcategory(@Param("subcategory") Subcategory subcategory, Pageable pageable);
}

