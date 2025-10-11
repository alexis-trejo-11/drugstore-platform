package microservice.order_service.orders.infrastructure.persistence.specification;

import jakarta.persistence.criteria.Predicate;
import microservice.order_service.orders.application.queries.request.SearchOrdersQuery;
import microservice.order_service.orders.infrastructure.persistence.models.OrderModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecifications {

    public static Specification<OrderModel> withSearchCriteria(SearchOrdersQuery query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // User ID filter
            if (query.userId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("userID").get("id"),
                        query.userId().value()
                ));
            }

            // Status filter
            if (query.status() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        query.status().name()
                ));
            }

            // Date range filter
            if (query.startDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        query.startDate()
                ));
            }
            if (query.endDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        query.endDate()
                ));
            }

            // Delivery method filter
            if (StringUtils.hasText(query.deliveryMethod())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("deliveryMethod"),
                        query.deliveryMethod()
                ));
            }

            // Shipping cost range
            if (query.minShippingCost() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("shippingCost"),
                        query.minShippingCost()
                ));
            }
            if (query.maxShippingCost() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("shippingCost"),
                        query.maxShippingCost()
                ));
            }

            // Exclude soft-deleted records
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}