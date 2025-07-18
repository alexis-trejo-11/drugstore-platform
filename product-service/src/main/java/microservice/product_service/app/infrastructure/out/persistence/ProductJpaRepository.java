package microservice.product_service.app.infrastructure.out.persistence;

import microservice.product_service.app.domain.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ProductJpaRepository  extends JpaRepository<ProductModel, UUID> {
    /**
    Finds a list of products by their category.
            * @param category The product category to search for.
            * @return A list of ProductModel objects belonging to the specified category.
     */
    List<ProductModel> findByCategory(ProductCategory category);

    /**
     * Finds a list of products by their manufacturer.
     * @param manufacturer The manufacturer name to search for.
     * @return A list of ProductModel objects from the specified manufacturer.
     */
    List<ProductModel> findByManufacturer(String manufacturer);

    /**
     * Finds a list of products whose name contains the given string, case-insensitively.
     * @param name The string to search for in product names.
     * @return A list of ProductModel objects whose names contain the specified string.
     */
    List<ProductModel> findByNameContainingIgnoreCase(String name);

    /**
     * Finds a list of products that have expired (expiration date is before today).
     * @param date The current date to compare against expiration dates.
     * @return A list of expired ProductModel objects.
     */
    List<ProductModel> findByExpirationDateBefore(LocalDate date);


    List<ProductModel> findByStockQuantityLessThan(int threshold);
}
