package microservice.cart_service.app.product.core.application;

import libs_kernel.response.Result;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import microservice.cart_service.app.product.core.domain.Product;
import microservice.cart_service.app.product.core.port.in.ProductUseCases;
import microservice.cart_service.app.product.core.port.out.ProductRepository;

import java.util.List;
import java.util.Map;

@Service
public class ProductService implements ProductUseCases {
  private final ProductRepository productRepository;
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductService.class);

  @Autowired
  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public List<ProductId> getUnavailableProductsIn(List<ProductId> productIds) {
    return productRepository.findAvailableByIdIn(productIds).stream()
				.map(Product::getId)
				.filter(id -> !productIds.contains(id))
				.toList();
  }

  @Override
  public List<ProductId> getOutOfStockProductsIn(List<ProductId> productIds) {
    // Placeholder implementation
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Page<Product> getProducts(Pageable pageable) {
    return productRepository.findProducts(pageable);
  }

  @Override
  public Product getProductById(String productId) {
    return productRepository.findProductById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " does not exist"));
  }

  @Override
  public void createProduct(ProductInsertCommand command) {
		log.info("Creating product with ID: {}", command.id());
		if (productRepository.existsById(command.id())) {
			log.error("Product with ID {} already exists", command.id());
			throw new IllegalArgumentException("Product with ID " + command.id() + " already exists");
		}
	  Product.CreateProductParams params = command.toParams();
		Product product = Product.create(params);

		Product savedProduct = productRepository.save(product);
		log.info("Product with ID: {} Persisted", savedProduct.getId());
  }

  @Override
  public void updateProduct(ProductInsertCommand command) {
    Product existingProduct = productRepository.findProductById(command.id())
				.orElseThrow(() -> new IllegalArgumentException("Product with ID " + command.id() + " does not exist"));

	  Product.CreateProductParams params = command.toParams();
	  Product product = Product.create(params);

		Product updatedProduct = productRepository.save(product);
		log.info("Product with ID: {} Updated", updatedProduct.getId());
  }

  @Override
  public void deleteProduct(ProductId productId) {
    Product product = productRepository.findProductById(productId.value())
				.orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId.value() + " does not exist"));

		productRepository.delete(product);
		log.info("Product with ID: {} Deleted", productId.value());
  }

  public Result<Void> validateAllExistAndAvailableByIdIn(Map<ProductId, Integer> productQuantitiesMap) {
    return Result.success();
  }
}
