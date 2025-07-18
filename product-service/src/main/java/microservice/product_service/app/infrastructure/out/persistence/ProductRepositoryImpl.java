package microservice.product_service.app.infrastructure.out.persistence;

import lombok.RequiredArgsConstructor;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.ProductCategory;
import microservice.product_service.app.domain.model.ProductId;
import microservice.product_service.app.domain.port.out.ProductRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Import for stream operations

@RequiredArgsConstructor
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository jpaRepository;

    @Override
    public Product save(Product product) {
        ProductModel model = ProductModel.from(product);
        ProductModel savedModel = jpaRepository.save(model);
        return savedModel.toDomain();
    }

    @Override
    public Optional<Product> findById(ProductId productId) {
        Optional<ProductModel> optionalProductModel = jpaRepository.findById(productId.value());
        return optionalProductModel.map(ProductModel::toDomain);
    }

    @Override
    public void deleteById(ProductId productId) {
        jpaRepository.deleteById(productId.value());
    }

    @Override
    public boolean existsById(ProductId productId) {
        return jpaRepository.existsById(productId.value());
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(ProductModel::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCategory(ProductCategory category) {
        return jpaRepository.findByCategory(category).stream()
                .map(ProductModel::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByManufacturer(String manufacturer) {
        return jpaRepository.findByManufacturer(manufacturer).stream()
                .map(ProductModel::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        return jpaRepository.findByNameContainingIgnoreCase(name).stream()
                .map(ProductModel::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findExpiredProducts() {
        return jpaRepository.findByExpirationDateBefore(LocalDate.now()).stream()
                .map(ProductModel::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findLowStockProducts(int threshold) {
        return jpaRepository.findByStockQuantityLessThan(threshold).stream()
                .map(ProductModel::toDomain)
                .collect(Collectors.toList());
    }
}
