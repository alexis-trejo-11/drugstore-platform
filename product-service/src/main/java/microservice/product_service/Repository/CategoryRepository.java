package microservice.product_service.Repository;


import microservice.product_service.Model.Category;
import microservice.product_service.Model.MainCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findById(Long categoryID, Pageable pageable);
    Page<Category> findByMainCategory(MainCategory mainCategory, Pageable pageable);
}
