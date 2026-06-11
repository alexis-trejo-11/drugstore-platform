package io.github.alexisTrejo11.drugstore.order.orders.application.query.request;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.OrderStatus;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;
import io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.request.OrderSearchRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@Builder
public record SearchOrdersQuery(
        UserID userId,
        OrderStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String deliveryMethod,
        Double minShippingCost,
        Double maxShippingCost,
        Pageable pageable
) {
    public static SearchOrdersQuery fromRequest(OrderSearchRequest request) {
        // Build Pageable from request
        Pageable pageable = PageRequest.of(
                request.page() != null ? request.page() : 0,
                request.size() != null ? request.size() : 20,
                Sort.by(Sort.Direction.fromString(
                                request.sortDirection() != null ? request.sortDirection() : "DESC"),
                        request.sortBy() != null ? request.sortBy() : "createdAt"
                )
        );

        return SearchOrdersQuery.builder()
                .userId(request.userId() != null ? UserID.of(request.userId()) : null)
                .status(request.status())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .deliveryMethod(request.deliveryMethod())
                .minShippingCost(request.minShippingCost())
                .maxShippingCost(request.maxShippingCost())
                .pageable(pageable)
                .build();
    }
}
