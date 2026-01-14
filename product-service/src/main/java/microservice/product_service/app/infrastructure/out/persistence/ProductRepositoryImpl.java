package microservice.product_service.app.infrastructure.out.persistence;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.valueobjects.ProductCode;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.application.port.out.ProductRepository;
import microservice.product_service.app.domain.specification.ProductSearchCriteria;
import microservice.product_service.app.infrastructure.out.persistence.mapper.ProductModelMapper;
import microservice.product_service.app.infrastructure.out.persistence.specification.ProductSpecificationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    ProductModel entity = mapper.fromDomain(product);
    ProductModel saved = jpaRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Product> findByID(ProductID id) {
    return jpaRepository.findById(id.value().toString())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Product> findByCode(ProductCode code) {
    Specification<ProductModel> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("code"),
        code.value());
    return jpaRepository.findOne(spec)
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
  public boolean existsByCode(ProductCode code) {
    return jpaRepository.existsByCode(code.value());
  }

  @Override
  public boolean existsByID(ProductID id) {
    return jpaRepository.existsById(id.value().toString());
  }

  @Override
  public void deleteByID(ProductID id) {
    jpaRepository.deleteById(id.value().toString());
  }

  @Override
  public void restoreByID(ProductID id) {
    Optional<ProductModel> optional = jpaRepository.findByIdIncludeDeleted(id.value().toString());
    if (optional.isPresent()) {
      ProductModel entity = optional.get();
      entity.markAsRestored();
      jpaRepository.save(entity);
    } else {
      log.warn("Attempted to restore non-existing product with id {}", id.value());
    }
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
