package microservice.store_service.infrastructure.adapter.outbound.persistence.specification;

import jakarta.persistence.criteria.Predicate;
import microservice.store_service.domain.specification.StoreSearchCriteria;
import microservice.store_service.infrastructure.adapter.outbound.persistence.entity.StoreEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class StoreSpecificationBuilder {

    public Specification<StoreEntity> build(StoreSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.exactCode() != null) {
                predicates.add(cb.equal(root.get("exactCode"), criteria.exactCode()));
            }

            if (criteria.nameLike() != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + criteria.nameLike().toLowerCase() + "%"
                ));
            }

            if (criteria.statuses() != null && !criteria.statuses().isEmpty()) {
                predicates.add(root.get("status").in(criteria.statuses()));
            }

            if (criteria.emailLike() != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("email")),
                        "%" + criteria.emailLike().toLowerCase() + "%"
                ));
            }

            if (criteria.phoneLike() != null) {
                predicates.add(cb.like(
                        root.get("phone"),
                        "%" + criteria.phoneLike() + "%"
                ));
            }

            // Exclude soft-deleted records
            predicates.add(cb.isNull(root.get("deletedAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
