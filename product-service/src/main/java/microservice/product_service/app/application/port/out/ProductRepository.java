package microservice.product_service.app.application.port.out;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.valueobjects.ProductCode;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.domain.specification.ProductSearchCriteria;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ProductRepository {
  Product save(Product product);

  Optional<Product> findByID(ProductID id);

  Optional<Product> findByCode(ProductCode code);

  Page<Product> search(ProductSearchCriteria criteria);

  long count(ProductSearchCriteria criteria);

  boolean existsByCode(ProductCode code);

  boolean existsByID(ProductID id);

  void deleteByID(ProductID id);

  void restoreByID(ProductID id);
}

/**
 * Port (Output) interface for managing Product entities within the domain.
 * This interface defines the contract that any persistence adapter must adhere
 * to,
 * allowing the application layer to interact with product data without knowing
 * the underlying storage technology.
 */
public interface ProductRepository {

  /**
   * Saves a given product to the persistent storage.
   * If the product already exists (based on its ID), it will be updated.
   * If the product is new, it will be created.
   *
   * @param product The product domain object to save. Must not be
   *                {@literal null}.
   * @return The saved product, which might have been updated (e.g., with an
   *         assigned ID if new).
   *         Will never be {@literal null}.
   */
  Product save(Product product);

  /**
   * Retrieves a product by its unique identifier.
   *
   * @param productId The {@link ProductID} of the product to retrieve. Must not
   *                  be {@literal null}.
   * @return An {@link Optional} containing the {@link Product} if found, or
   *         {@link Optional#empty()} if no product with the given ID exists.
   */
  Optional<Product> findById(ProductID productId);

  /**
   * Deletes a product from the persistent storage by its unique identifier.
   * If the product does not exist, the operation will complete without error.
   *
   * @param productId The {@link ProductID} of the product to delete. Must not be
   *                  {@literal null}.
   */
  void deleteById(ProductID productId);

  /**
   * Checks if a product with the given unique identifier exists in the persistent
   * storage.
   *
   * @param productId The {@link ProductID} of the product to check for existence.
   *                  Must not be {@literal null}.
   * @return {@code true} if a product with the given ID exists, {@code false}
   *         otherwise.
   */
  boolean existsById(ProductID productId);

  /**
   * Retrieves all products from the persistent storage.
   *
   * @return A {@link List} of all {@link Product} domain objects found. Returns
   *         an empty list if no products exist.
   */
  List<Product> findAll();

  /**
   * Retrieves a list of products belonging to a specific category.
   *
   * @param category The {@link ProductCategory} to filter products by. Must not
   *                 be {@literal null}.
   * @return A {@link List} of {@link Product} domain objects that belong to the
   *         specified category.
   *         Returns an empty list if no products are found for the category.
   */
  List<Product> findByCategory(ProductCategory category);

  /**
   * Retrieves a list of products from a specific manufacturer.
   *
   * @param manufacturer The name of the manufacturer to filter products by. Must
   *                     not be {@literal null} or empty.
   * @return A {@link List} of {@link Product} domain objects from the specified
   *         manufacturer.
   *         Returns an empty list if no products are found for the manufacturer.
   */
  List<Product> findByManufacturer(String manufacturer);

  /**
   * Retrieves a list of products whose name contains the given string,
   * case-insensitively.
   * This allows for partial name searches.
   *
   * @param name The string to search for within product names. Must not be
   *             {@literal null} or empty.
   * @return A {@link List} of {@link Product} domain objects whose names contain
   *         the specified string.
   *         Returns an empty list if no products match the criteria.
   */
  List<Product> findByNameContaining(String name);

  /**
   * Retrieves a list of products that have passed their expiration date.
   * The comparison is typically made against the current date.
   *
   * @return A {@link List} of {@link Product} domain objects that are expired.
   *         Returns an empty list if no expired products are found.
   */
  List<Product> findExpiredProducts();

  /**
   * Retrieves a list of products whose stock quantity is below a specified
   * threshold.
   * This is useful for identifying products that need to be reordered.
   *
   * @param threshold The maximum stock quantity to consider a product as "low
   *                  stock".
   *                  Must be a non-negative integer.
   * @return A {@link List} of {@link Product} domain objects with stock quantity
   *         less than the threshold.
   *         Returns an empty list if no products are found with low stock.
   */
  List<Product> findLowStockProducts(int threshold);
}
