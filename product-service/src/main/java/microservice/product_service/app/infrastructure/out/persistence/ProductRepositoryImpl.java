package microservice.product_service.app.infrastructure.out.persistence;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import microservice.product_service.app.application.port.output.ProductRepository;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.domain.model.valueobjects.SKU;
import microservice.product_service.app.domain.specification.ProductSearchCriteria;
import microservice.product_service.app.infrastructure.out.persistence.mapper.ProductModelMapper;
import microservice.product_service.app.infrastructure.out.persistence.specification.ProductSpecificationBuilder;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
  private static final Logger log = LoggerFactory.getLogger(ProductRepositoryImpl.class);

  private final ProductJpaRepository jpaRepository;
  private final ProductModelMapper mapper;
  private final ProductSpecificationBuilder specificationBuilder;

  @Autowired
  public ProductRepositoryImpl(
      ProductJpaRepository jpaRepository,
      ProductModelMapper mapper,
      ProductSpecificationBuilder specificationBuilder) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
    this.specificationBuilder = specificationBuilder;
  }

  @Override
  public Product save(Product product) {
    log.debug("Saving product with ID: {}", product.getId());
    ProductModel entity = mapper.fromDomain(product);

    ProductModel saved = jpaRepository.save(entity);
    log.debug("Product saved with ID: {}", saved.getId());

    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Product> findByID(ProductID id) {
    return jpaRepository.findByIdAndDeletedAtIsNull(id.value())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Product> findBySKU(SKU sku) {
    Specification<ProductModel> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("sku"),
        sku.value());
    return jpaRepository.findOne(spec)
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Product> findByBarCode(String barcode) {
    return jpaRepository.findByBarcodeAndDeletedAtIsNull(barcode)
        .map(mapper::toDomain);
  }

  @Override
  public Page<Product> search(ProductSearchCriteria criteria) {
    Specification<ProductModel> spec = specificationBuilder.build(criteria);
    Page<ProductModel> page = jpaRepository.findAll(spec, buildPageRequest(criteria));
    return mapper.toDomainPage(page);
  }

  @Override
  public long count(ProductSearchCriteria criteria) {
    Specification<ProductModel> spec = specificationBuilder.build(criteria);
    return jpaRepository.count(spec);
  }

  @Override
  public boolean existsBySKU(SKU sku) {
    return jpaRepository.existsBySKU(sku.value());
  }

  @Override
  public boolean existsByID(ProductID id) {
    return jpaRepository.existsById(id.value());
  }

  @Override
  public void deleteByID(ProductID id) {
    jpaRepository.deleteById(id.value());
  }

  @Override
  public Optional<Product> findDeletedByID(ProductID id) {
    return jpaRepository.findByIdAndDeletedAtNotNull(id.value())
        .map(mapper::toDomain);
  }

  @Override
  public boolean existsByBarCode(String barcode) {
    return jpaRepository.existsByBarcode(barcode);
  }

  private PageRequest buildPageRequest(ProductSearchCriteria criteria) {
    Sort sort = buildSort(criteria);
    return PageRequest.of(
        criteria.page(),
        criteria.size(),
        sort);
  }

  private Sort buildSort(ProductSearchCriteria criteria) {
    if (criteria.sortBy() == null) {
      return Sort.unsorted();
    }

    String field = criteria.sortBy().field();
    String direction = criteria.sortBy().direction();

    return "ASC".equals(direction)
        ? Sort.by(field).ascending()
        : Sort.by(field).descending();
  }
}
