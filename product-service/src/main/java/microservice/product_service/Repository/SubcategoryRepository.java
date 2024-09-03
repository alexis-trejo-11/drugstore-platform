package microservice.product_service.Repository;

import microservice.product_service.Model.MainCategory;
import microservice.product_service.Model.Subcategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    @Override
    Page<Subcategory> findAll(Pageable pageable);
    Page<Subcategory> findByMainCategory(MainCategory mainCategory, Pageable pageable);
}
