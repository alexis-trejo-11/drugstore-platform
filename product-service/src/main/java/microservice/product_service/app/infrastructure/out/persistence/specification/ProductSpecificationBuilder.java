package microservice.product_service.app.infrastructure.out.persistence.specification;

import microservice.product_service.app.domain.specification.ProductSearchCriteria;
import microservice.product_service.app.infrastructure.out.persistence.ProductModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductSpecificationBuilder {

  public Specification<ProductModel> build(ProductSearchCriteria criteria) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (criteria.name() != null && !criteria.name().isBlank()) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + criteria.name().toLowerCase() + "%"));
      }

      if (criteria.category() != null) {
        predicates.add(
            criteriaBuilder.equal(root.get("category"), criteria.category()));
      }

      if (criteria.manufacturer() != null && !criteria.manufacturer().isBlank()) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("manufacturer")),
                "%" + criteria.manufacturer().toLowerCase() + "%"));
      }

      if (criteria.requiresPrescription() != null) {
        predicates.add(
            criteriaBuilder.equal(root.get("requiresPrescription"), criteria.requiresPrescription()));
      }

      if (Boolean.TRUE.equals(criteria.onlyActive())) {
        predicates.add(
            criteriaBuilder.equal(root.get("status"),
                microservice.product_service.app.domain.model.enums.ProductStatus.ACTIVE));
      }

      if (Boolean.TRUE.equals(criteria.excludeDeleted())) {
        predicates.add(
            criteriaBuilder.isNull(root.get("deletedAt")));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
