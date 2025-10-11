package microservice.order_service.orders.application.queries.mapper;

import libs_kernel.mapper.ResultMapper;
import microservice.order_service.orders.application.queries.response.OrderQueryResult;
import microservice.order_service.orders.domain.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class OrderQueryMapper implements ResultMapper<OrderQueryResult, Order> {

    @Override
    public OrderQueryResult toResult(Order order) {
        return OrderQueryResult.builder()
                .id(order.getId() != null ? order.getId() : null)
                .userID(order.getUserID() != null ? order.getUserID(): null)
                .status(order.getStatus() != null ? order.getStatus(): null)
                .totalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : null)
                .deliveryMethod(order.getDeliveryMethod() != null ? order.getDeliveryMethod() : null)
                .totalItems(order.getTotalItemsCount())
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt() : null)
                .build();
    }

    @Override
    public List<OrderQueryResult> toResults(List<Order> entities) {
        return entities.stream()
                .map(this::toResult)
                .toList();
    }

    @Override
    public Page<OrderQueryResult> toResultPage(Page<Order> entityPage) {
        return entityPage.map(this::toResult);
    }

}