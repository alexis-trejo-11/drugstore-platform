package microservice.order_service.orders.application.queries.mapper;

import libs_kernel.mapper.DomainMapper;

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
                .id(order.getId())
                .userID(order.getUser().getId() != null ? order.getUser().getId() : null)
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount().amount())
                .currency(order.getTotalAmount().currency().getDisplayName())
                .deliveryMethod(order.getDeliveryMethod())
                .totalItems(order.getTotalItemsCount())
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