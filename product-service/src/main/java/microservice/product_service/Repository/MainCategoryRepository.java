package microservice.product_service.Repository;


import microservice.product_service.Model.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {
}
