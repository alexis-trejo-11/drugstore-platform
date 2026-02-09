package microservice.cart_service.app.product.adapter.output.persistence;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.product.core.domain.Product;
import microservice.cart_service.app.product.core.port.out.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
	private final ProductJpaRepository jpaRepository;
	private final ProductModelMapper mapper;

	@Autowired
	public ProductRepositoryImpl(ProductJpaRepository jpaRepository, ProductModelMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Page<Product> findProducts(Pageable pageable) {
			Page<ProductModel> productModels = jpaRepository.findAll(pageable);
			return productModels.map(mapper::toDomain);
	}

	@Override
	public Optional<Product> findProductById(String productId) {
		return jpaRepository.findById(productId)
				.map(mapper::toDomain);
	}

	@Override
	public Product save(Product product) {
		ProductModel productModel = mapper.toJpaModel(product);
		ProductModel savedModel = jpaRepository.save(productModel);
		return mapper.toDomain(savedModel);
	}

	@Override
	public void delete(Product product) {
		jpaRepository.deleteById(product.getId().value());
	}

	@Override
	public List<Product> findAvailableByIdIn(List<ProductId> productIds) {
		List<String> ids = productIds.stream()
				.map(ProductId::value)
				.toList();
		List<ProductModel> productModels = jpaRepository.findAvailableByIdIn(ids);
		return mapper.toDomains(productModels);
	}

	@Override
	public boolean existsById(String productId) {
		return jpaRepository.existsById(productId);
	}
}
